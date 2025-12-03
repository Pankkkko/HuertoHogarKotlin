package React.HuertoHogar.config;

import React.HuertoHogar.model.Producto;
import React.HuertoHogar.repository.ProductoRepository;
import React.HuertoHogar.service.UsuarioService;
import React.HuertoHogar.model.Usuario;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductoRepository productoRepository;
    private final UsuarioService usuarioService;

    public DataInitializer(ProductoRepository productoRepository, UsuarioService usuarioService) {
        this.productoRepository = productoRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) throws Exception {
        long count = productoRepository.count();
        System.out.println("[DataInitializer] productos en DB: " + count);
        if (count == 0) {
            System.out.println("[DataInitializer] insertando productos de ejemplo...");

            Producto p1 = new Producto();
            p1.setNombre("Manzanas Fuji");
            p1.setCategoria("frutas");
            p1.setPrecio(1200.0);
            p1.setStock(50);
            p1.setDescripcion("Manzanas Fuji crujientes y dulces, cultivadas en el Valle del Maule. Perfectas para meriendas saludables o como ingrediente en postres.");
            p1.setActivo(true);
            p1.setImagen("/manzanafuji1.jpg");
            p1.setImagen2("/manzanafuji2.jpg");
            p1.setImagen3("/manzanafuji3.jpg");
            p1.setImagen4("/manzanafuji4.webp");

            Producto p2 = new Producto();
            p2.setNombre("Naranjas Valencianas");
            p2.setCategoria("frutas");
            p2.setPrecio(1000.0);
            p2.setStock(50);
            p2.setDescripcion("Jugosas y ricas en vitamina C, ideales para zumos frescos y refrescantes.");
            p2.setActivo(true);
            p2.setImagen("/naranja1_1.webp");
            p2.setImagen2("/naranja1.jpg");
            p2.setImagen3("/naranja4.jpg");
            p2.setImagen4("/naranja1.jpg");

            Producto p3 = new Producto();
            p3.setNombre("Platanos Cavendish");
            p3.setCategoria("frutas");
            p3.setPrecio(800.0);
            p3.setStock(60);
            p3.setDescripcion("Plátanos maduros y dulces, perfectos para el desayuno o como snack energético.");
            p3.setActivo(true);
            p3.setImagen("/banana1.jpg");
            p3.setImagen2("/banana2.webp");
            p3.setImagen3("/banana3.jpeg");
            p3.setImagen4("/banana4.webp");

            Producto p4 = new Producto();
            p4.setNombre("Zanahorias Organicas");
            p4.setCategoria("verduras");
            p4.setPrecio(900.0);
            p4.setStock(40);
            p4.setDescripcion("Zanahorias crujientes cultivadas sin pesticidas en la Región de O'Higgins.");
            p4.setActivo(true);
            p4.setImagen("/verdura1.jpg");
            p4.setImagen2("/zanahoria2.jfif");
            p4.setImagen3("/zanahoria3.webp");
            p4.setImagen4("/zanahoria4.jpg");

            Producto p5 = new Producto();
            p5.setNombre("Espinacas frescas");
            p5.setCategoria("verduras");
            p5.setPrecio(700.0);
            p5.setStock(45);
            p5.setDescripcion("Espinacas frescas y nutritivas, perfectas para ensaladas y batidos verdes.");
            p5.setActivo(true);
            p5.setImagen("/espinaca1.jpg");
            p5.setImagen2("/espinaca2.webp");
            p5.setImagen3("/espinaca3.jpg");
            p5.setImagen4("/espinaca4.jfif");

            Producto p6 = new Producto();
            p6.setNombre("Pimientos Tricolores");
            p6.setCategoria("verduras");
            p6.setPrecio(1500.0);
            p6.setStock(30);
            p6.setDescripcion("Pimientos rojos, amarillos y verdes, ideales para salteados y platos coloridos.");
            p6.setActivo(true);
            p6.setImagen("/pimientos1.jpg");
            p6.setImagen2("/pimientos2.jpg");
            p6.setImagen3("/pimientos3.webp");
            p6.setImagen4("/pimientos4.webp");

            Producto p7 = new Producto();
            p7.setNombre("Miel Organica");
            p7.setCategoria("organicos");
            p7.setPrecio(5000.0);
            p7.setStock(20);
            p7.setDescripcion("Miel pura y orgánica producida por apicultores locales. Rica en antioxidantes y con un sabor inigualable.");
            p7.setActivo(true);
            p7.setImagen("/miel1.jpg");
            p7.setImagen2("/miel2.jpg");
            p7.setImagen3("/miel3.jpg");
            p7.setImagen4("/miel4.webp");

            productoRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7));
            System.out.println("[DataInitializer] productos guardados.");
        } else {
            System.out.println("[DataInitializer] no se requieren inserciones.");
        }

        // Si ya existen productos, intentar completar campos de imagen si están vacíos
        try {
                productoRepository.findAll().forEach(p -> {
                boolean changed = false;
                String name = p.getNombre() == null ? "" : p.getNombre().toLowerCase();
                if ((p.getImagen() == null || p.getImagen().isBlank())) {
                    if (name.contains("manz")) { p.setImagen("/manzanafuji1.jpg"); p.setImagen2("/manzanafuji2.jpg"); p.setImagen3("/manzanafuji3.jpg"); p.setImagen4("/manzanafuji4.webp"); changed = true; }
                    else if (name.contains("naranj")) { p.setImagen("/naranja1_1.webp"); p.setImagen2("/naranja1.jpg"); p.setImagen3("/naranja4.jpg"); p.setImagen4("/naranja1.jpg"); changed = true; }
                    else if (name.contains("platan") || name.contains("banana")) { p.setImagen("/banana1.jpg"); p.setImagen2("/banana2.webp"); p.setImagen3("/banana3.jpeg"); p.setImagen4("/banana4.webp"); changed = true; }
                    else if (name.contains("zanah")) { p.setImagen("/verdura1.jpg"); p.setImagen2("/zanahoria2.jfif"); p.setImagen3("/zanahoria3.webp"); p.setImagen4("/zanahoria4.jpg"); changed = true; }
                    else if (name.contains("espin")) { p.setImagen("/espinaca1.jpg"); p.setImagen2("/espinaca2.webp"); p.setImagen3("/espinaca3.jpg"); p.setImagen4("/espinaca4.jfif"); changed = true; }
                    else if (name.contains("pimiento")) { p.setImagen("/pimientos1.jpg"); p.setImagen2("/pimientos2.jpg"); p.setImagen3("/pimientos3.webp"); p.setImagen4("/pimientos4.webp"); changed = true; }
                    else if (name.contains("miel")) { p.setImagen("/miel1.jpg"); p.setImagen2("/miel2.jpg"); p.setImagen3("/miel3.jpg"); p.setImagen4("/miel4.webp"); changed = true; }
                }
                if (changed) {
                    productoRepository.save(p);
                    System.out.println("[DataInitializer] actualizado imagenes para producto: " + p.getNombre());
                }
                // Normalizar categorías antiguas a los códigos del frontend
                String cat = p.getCategoria() == null ? "" : p.getCategoria().trim();
                String catLower = cat.toLowerCase();
                String normalized = null;
                if (catLower.equals("frutas frescas") || catLower.equals("frutas")) normalized = "frutas";
                else if (catLower.equals("verduras organicas") || catLower.equals("verduras")) normalized = "verduras";
                else if (catLower.equals("productos organicos") || catLower.equals("productos organicos")) normalized = "organicos";
                else if (catLower.equals("productos lacteos") || catLower.equals("lacteos")) normalized = "lacteos";
                if (normalized != null && !normalized.equals(cat)) {
                    p.setCategoria(normalized);
                    productoRepository.save(p);
                    System.out.println("[DataInitializer] normalizada categoria para producto: " + p.getNombre() + " -> " + normalized);
                }
            });
        } catch (Exception ex) {
            System.err.println("[DataInitializer] error actualizando imágenes existentes: " + ex.getMessage());
        }

        // Insertar usuarios de ejemplo si no existen
        try {
            if (usuarioService.findAll().isEmpty()) {
                System.out.println("[DataInitializer] insertando usuarios de ejemplo...");
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setRol("admin");
                admin.setPassword("admin123");
                admin.setActivo(true);

                Usuario user = new Usuario();
                user.setNombre("Juan Perez");
                user.setEmail("juan@ejemplo.com");
                user.setRol("user");
                user.setPassword("user123");
                user.setActivo(true);

                usuarioService.save(admin);
                usuarioService.save(user);
                System.out.println("[DataInitializer] usuarios guardados.");
            }
        } catch (Exception ex) {
            System.err.println("[DataInitializer] error insertando usuarios: " + ex.getMessage());
        }

        // Mostrar tablas existentes (consultando el repositorio para forzar inicialización)
        try {
            productoRepository.findAll().forEach(p -> {});
            System.out.println("[DataInitializer] findAll ejecutado correctamente.");
        } catch (Exception ex) {
            System.err.println("[DataInitializer] error al ejecutar findAll: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
