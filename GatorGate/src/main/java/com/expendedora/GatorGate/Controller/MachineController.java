package com.expendedora.GatorGate.Controller;

import com.expendedora.GatorGate.Model.BuyRequest;
import com.expendedora.GatorGate.Model.Machine;
import com.expendedora.GatorGate.Model.MachineWithProducts;
import com.expendedora.GatorGate.Model.Product;
import com.expendedora.GatorGate.Repository.RepositoryMachine;
import com.expendedora.GatorGate.Repository.RepositoryProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Optional;

// Controlador para la clase Machine
@RestController
@RequestMapping("/machines")
@CrossOrigin// ojo soluciona el error del origen de la informacion
public class MachineController {


    @Autowired
    private RepositoryMachine machineRepository;
    @Autowired
    private RepositoryProduct productRepository;

    @GetMapping("/getallmachines")   // <------ en revision y prueba
    @CrossOrigin
    public List<Machine> getAllMachines() {
        List<Machine> machines = machineRepository.findAll();
        String asignament=" ";
        for(Machine machine: machines ){
            if(machine.getStatus()==null){
                machine.setStatus(asignament);
                machineRepository.save(machine);
            }

        }

        // Devolver todas las máquinas actualizadas
        return machineRepository.findAll();


    }


    @GetMapping("/{machineId}/getMachineInfo")
    @CrossOrigin
    public ResponseEntity<MachineWithProducts> getMachineInfo(@PathVariable long machineId) {
        Optional<Machine> optionalMachine = machineRepository.findById(machineId);

        if (optionalMachine.isPresent()) {
            Machine machine = optionalMachine.get();

            // Crea un objeto que contenga la información de la máquina y sus productos
            MachineWithProducts machineWithProducts = new MachineWithProducts();
            machineWithProducts.setId(machine.getId());
            machineWithProducts.setLocation(machine.getLocation());
            machineWithProducts.setStonks(machine.getStonks());
            machineWithProducts.setMinimalproduct(machine.getMinimalproduct());
            machineWithProducts.setProducts(machine.getProducts());

            return ResponseEntity.ok(machineWithProducts);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/changemachinestatus/{id}")
    // cross origin genera errores del tipo 400 ojo con eso
    public ResponseEntity<String> changeMachineStatus(@PathVariable long id, @RequestParam String newStatus) {
        Optional<Machine> machineOptional = machineRepository.findById(id);
        if (machineOptional.isPresent()) {

            Machine machine = machineOptional.get();

            String st= machine.getStatus();
            if(machine.getMinimalproduct()>45){
                st=" abastecida, "+machine.getStatus();
            }
            else{
                st=" Necesita reabastecer,  "+machine.getStatus();
            }
            // Establecer el nuevo estado
            machine.setStatus(st+ "\n"+newStatus);

            System.out.println(machine.getStatus());

            // Guardar los cambios en la base de datos
            machineRepository.save(machine);
            return ResponseEntity.ok("El estado de la máquina con ID " + id + " se ha actualizado correctamente.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }







    // es importante encapsular la respuesta para poder acceder al endpoint de manera correcta

    @PostMapping("/{machineId}/buy")
    public ResponseEntity<String> buyProduct (@RequestBody BuyRequest buyrequest) {
     long machineId=buyrequest.getIdmachine();
     long productid=buyrequest.getIdproduct();
     int quantity=buyrequest.getQuantity();
     int minimal =0;

      Optional<Machine> optionalMachine = machineRepository.findById(machineId);


        if (optionalMachine.isPresent()) {
            Machine machine = optionalMachine.get();
            minimal=machine.calculateMinimalProduct()-quantity;
            machine.setMinimalproduct(minimal);

            Optional<Product> optionalProduct = productRepository.findById(productid);
            if(optionalProduct.isPresent()){

               Product product=optionalProduct.get();
               // verifica si el producto es suficiente
               if(quantity>product.getQuantity()){
                   return ResponseEntity.notFound().build();

               }
               else {

                   System.out.println("calculo del producto de la maquina antes de la venta " + machine.getMinimalproduct());
                   product.setQuantity(product.getQuantity() - quantity);
                   System.out.println("calculo del producto ya restando la cantidad solicitada " + product.getQuantity());
                   product.setQuantitysold(product.getQuantitysold() + quantity);



                   System.out.println("calculo del producto vendido " + product.getQuantitysold());
                   System.out.println("calculo del producto en la maquina " + machine.getMinimalproduct());

                   System.out.println("calculo del producto en la maquina despues del calculo " + machine.getMinimalproduct());

               }



            }


                machineRepository.save(machine);



            // Lógica para validar y procesar la compra en función de buyrequest

            // Actualiza la máquina en la base de datos
            //machine.setMinimalproduct(machine.calculateMinimalProduct());
            //machineRepository.save(machine);

            //
            return ResponseEntity.ok("La compra se ha realizado con éxito");
        } else {
            return ResponseEntity.notFound().build();
        }
    }






    // comprobado y funcional
    @PostMapping("/createmachine")
    @CrossOrigin
    public ResponseEntity<Machine> createMachine(@RequestBody Machine machine) {
        machine.calculateMinimalProduct();  // verificando
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






}// end  Class Machine Controller
