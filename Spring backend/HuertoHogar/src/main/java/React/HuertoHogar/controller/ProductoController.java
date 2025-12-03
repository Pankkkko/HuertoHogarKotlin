package React.HuertoHogar.controller;

import React.HuertoHogar.model.Producto;
import React.HuertoHogar.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:5173") // permitir peticiones desde Vite dev server
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> getAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> create(@RequestBody Producto producto) {
        Producto saved = productoService.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Crear producto con imágenes (multipart/form-data)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Producto> createWithImages(
            @RequestParam String nombre,
            @RequestParam String categoria,
            @RequestParam Double precio,
            @RequestParam Integer stock,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) MultipartFile imagen,
            @RequestParam(required = false) MultipartFile imagen2,
            @RequestParam(required = false) MultipartFile imagen3,
            @RequestParam(required = false) MultipartFile imagen4
    ) throws IOException {
        System.out.println("[ProductoController.createWithImages] Received createWithImages request: nombre="+nombre+" categoria="+categoria+" precio="+precio+" stock="+stock+" descripcion="+descripcion+" activo="+activo);
        if (imagen != null) System.out.println(" - imagen: " + imagen.getOriginalFilename() + " (size=" + imagen.getSize() + ")");
        if (imagen2 != null) System.out.println(" - imagen2: " + imagen2.getOriginalFilename() + " (size=" + imagen2.getSize() + ")");
        if (imagen3 != null) System.out.println(" - imagen3: " + imagen3.getOriginalFilename() + " (size=" + imagen3.getSize() + ")");
        if (imagen4 != null) System.out.println(" - imagen4: " + imagen4.getOriginalFilename() + " (size=" + imagen4.getSize() + ")");
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setDescripcion(descripcion);
        producto.setActivo(activo == null ? true : activo);

        // Guardar archivos y asignar URLs
        if (imagen != null && !imagen.isEmpty()) producto.setImagen(saveFile(imagen));
        if (imagen2 != null && !imagen2.isEmpty()) producto.setImagen2(saveFile(imagen2));
        if (imagen3 != null && !imagen3.isEmpty()) producto.setImagen3(saveFile(imagen3));
        if (imagen4 != null && !imagen4.isEmpty()) producto.setImagen4(saveFile(imagen4));

        Producto saved = productoService.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Endpoint temporal para debug: imprime headers y partes recibidas
    @PostMapping(path = "/debug-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> debugUpload(HttpServletRequest request) {
        try {
            System.out.println("[ProductoController.debugUpload] Headers:");
            request.getHeaderNames().asIterator().forEachRemaining(h -> System.out.println("  " + h + ": " + request.getHeader(h)));
            System.out.println("[ProductoController.debugUpload] Parts:");
            for (Part p : request.getParts()) {
                System.out.println("  part: name=" + p.getName() + " filename=" + p.getSubmittedFileName() + " size=" + p.getSize());
            }
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(@PathVariable Long id, @RequestBody Producto producto) {
        Producto updated = productoService.update(id, producto);
        return ResponseEntity.ok(updated);
    }

    // Actualizar producto y opcionalmente reemplazar imágenes
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Producto> updateWithImages(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String categoria,
            @RequestParam Double precio,
            @RequestParam Integer stock,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) MultipartFile imagen,
            @RequestParam(required = false) MultipartFile imagen2,
            @RequestParam(required = false) MultipartFile imagen3,
            @RequestParam(required = false) MultipartFile imagen4
    ) throws IOException {
        System.out.println("[ProductoController.updateWithImages] Received updateWithImages request id="+id+" nombre="+nombre+" categoria="+categoria+" precio="+precio+" stock="+stock+" descripcion="+descripcion+" activo="+activo);
        if (imagen != null) System.out.println(" - imagen: " + imagen.getOriginalFilename() + " (size=" + imagen.getSize() + ")");
        if (imagen2 != null) System.out.println(" - imagen2: " + imagen2.getOriginalFilename() + " (size=" + imagen2.getSize() + ")");
        if (imagen3 != null) System.out.println(" - imagen3: " + imagen3.getOriginalFilename() + " (size=" + imagen3.getSize() + ")");
        if (imagen4 != null) System.out.println(" - imagen4: " + imagen4.getOriginalFilename() + " (size=" + imagen4.getSize() + ")");
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setDescripcion(descripcion);
        producto.setActivo(activo == null ? true : activo);

        if (imagen != null && !imagen.isEmpty()) producto.setImagen(saveFile(imagen));
        if (imagen2 != null && !imagen2.isEmpty()) producto.setImagen2(saveFile(imagen2));
        if (imagen3 != null && !imagen3.isEmpty()) producto.setImagen3(saveFile(imagen3));
        if (imagen4 != null && !imagen4.isEmpty()) producto.setImagen4(saveFile(imagen4));

        Producto updated = productoService.update(id, producto);
        return ResponseEntity.ok(updated);
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("[ProductoController.saveFile] Saved file to: " + target.toAbsolutePath() + " (size=" + Files.size(target) + ")");
        // URL accesible por el frontend (usar host del backend)
        String base = "http://localhost:8080";
        return base + "/uploads/" + filename;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Subir/actualizar solo imágenes de un producto existente
    @PostMapping(value = "/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Producto> uploadImagesForProduct(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile imagen,
            @RequestParam(required = false) MultipartFile imagen2,
            @RequestParam(required = false) MultipartFile imagen3,
            @RequestParam(required = false) MultipartFile imagen4
    ) throws IOException {
        System.out.println("[ProductoController.uploadImagesForProduct] id=" + id);
        java.util.Optional<Producto> opt = productoService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Producto existing = opt.get();
        try {
            if (imagen != null && !imagen.isEmpty()) existing.setImagen(saveFile(imagen));
            if (imagen2 != null && !imagen2.isEmpty()) existing.setImagen2(saveFile(imagen2));
            if (imagen3 != null && !imagen3.isEmpty()) existing.setImagen3(saveFile(imagen3));
            if (imagen4 != null && !imagen4.isEmpty()) existing.setImagen4(saveFile(imagen4));
            Producto saved = productoService.save(existing);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
