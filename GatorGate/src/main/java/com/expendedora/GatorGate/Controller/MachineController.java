package com.expendedora.GatorGate.Controller;

import com.expendedora.GatorGate.Model.Machine;
import com.expendedora.GatorGate.Model.Product;
import com.expendedora.GatorGate.Repository.RepositoryMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Controlador para la clase Machine
@RestController
@RequestMapping("/machines")
@CrossOrigin // ojo soluciona el error del origen de la informacion
public class MachineController {


    @Autowired
    private RepositoryMachine machineRepository;

    @GetMapping("/getallmachines")
    @CrossOrigin
    public List<Machine> getAllMachines() {
        return machineRepository.findAll();
    }




    @CrossOrigin
    @GetMapping("/{id}/getmachine")
    public ResponseEntity<Machine> Sellrmachine(@PathVariable long id, @RequestParam int quantityToBuy) {
        Optional<Machine> optionalMachine = machineRepository.findById(id);

        if (optionalMachine.isPresent()) {
            Machine machine = optionalMachine.get();

            // Aquí puedes implementar la lógica para realizar la compra de la cantidad especificada
            // Puedes ajustar esta parte según tu lógica de negocio específica

            // Por ejemplo, podrías verificar si la cantidad disponible es suficiente para la compra
            // y luego actualizar la cantidad de productos y otros detalles según sea necesario

            // Ejemplo (esto es solo una guía, ajusta según tus necesidades):
            for (Product product : machine.getProducts()) {
                // Asumiendo que cada producto tiene una cantidad disponible
                if (product.getQuantity() >= quantityToBuy) {
                    product.setQuantity(product.getQuantity() - quantityToBuy);
                } else {
                    // Manejar el caso en el que no hay suficientes productos disponibles
                    return ResponseEntity.badRequest().body(machine);
                }
            }

            // Actualizar la máquina en la base de datos
            machineRepository.save(machine);

            return ResponseEntity.ok(machine);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    // comprobado y funcional
    @PostMapping("/createmachine")
    @CrossOrigin
    public ResponseEntity<Machine> createMachine(@RequestBody Machine machine) {
        Machine savedMachine = machineRepository.save(machine);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMachine);
    }

    /// comprobado y funcional
    // se añadio la funcionalidad de obtener el minimo de stock para cada maquina 7/3/24
    @PostMapping("/{machineId}/addProducts")
    public ResponseEntity<Machine> addProductsToMachine(@PathVariable long machineId, @RequestBody List<Product> products) {
        Optional<Machine> optionalMachine = machineRepository.findById(machineId);

        if (optionalMachine.isPresent()) {
            Machine machine = optionalMachine.get();

            // Asigna la máquina a cada producto y agrega los productos a la lista de la máquina
            for (Product product : products) {
                product.setMachine(machine);
                machine.getProducts().add(product);
            }
            machine.setMinimalproduct(machine.calculateMinimalProduct());

            // Actualiza la máquina en la base de datos
            machineRepository.save(machine);

            return ResponseEntity.ok(machine);
        } else {
            return ResponseEntity.notFound().build();
        }
    }// end addproductsMachine





    /// metodo para actualizar
    // comprobado y listo

    @PutMapping("/{machineId}/updateProducts")
    public ResponseEntity<Machine> updateProductsInMachine(@PathVariable long machineId, @RequestBody List<Product> newProducts) {
        Optional<Machine> optionalMachine = machineRepository.findById(machineId);

        if (optionalMachine.isPresent()) {
            Machine machine = optionalMachine.get();

            // Borra la lista actual de productos asociados a la máquina
            machine.getProducts().clear();

            // Asigna la máquina a cada producto y agrega los productos a la lista de la máquina
            for (Product newProduct : newProducts) {
                newProduct.setMachine(machine);
                machine.getProducts().add(newProduct);
            }

            // Actualiza la máquina en la base de datos
            machineRepository.save(machine);

            return ResponseEntity.ok(machine);
        } else {
            return ResponseEntity.notFound().build();
        }
    }///


    @GetMapping("/{machineId}/stonks")
    @CrossOrigin
    public ResponseEntity<Double> calculateTotalPrice(@PathVariable long machineId) {
        Optional<Machine> optionalMachine = machineRepository.findById(machineId);

        if (optionalMachine.isPresent()) {
            Machine machine = optionalMachine.get();
            double total = 0.0;

            // Itera sobre la lista de productos y realiza la operación
            for (Product product : machine.getProducts()) {
                int newQuantity =product.getQuantitysold() ;
                double totalPrice = newQuantity * (product.getSalePrice()- product.getPurchasePrice());
                total += totalPrice;
            }
            machine.setStonks(total);
            machineRepository.save(machine);


            return ResponseEntity.ok(total);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /// fin calcular ganancias


    //metodo para calcular la maquina que mas vende
    @GetMapping("/top-seller")
    @CrossOrigin
    public ResponseEntity<Machine> findTopSellingMachine() {
        List<Machine> machines = machineRepository.findAll();

        //  variables para realizar un seguimiento de la máquina con más ventas
        Machine topSellingMachine = null;
        double maxSales = Double.MIN_VALUE;

        for (Machine machine : machines) {
            double totalSales = machine.getStonks();

            if (totalSales > maxSales) {
                maxSales = totalSales;
                topSellingMachine = machine;
            }
        }

        if (topSellingMachine != null) {
            return ResponseEntity.ok(topSellingMachine);
        } else {
            return ResponseEntity.notFound().build();
        }
    }






}// fin controlador
