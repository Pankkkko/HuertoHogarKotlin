package React.HuertoHogar.service;

import React.HuertoHogar.model.Producto;
import React.HuertoHogar.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto update(Long id, Producto producto) {
        return productoRepository.findById(id).map(existing -> {
            existing.setNombre(producto.getNombre());
            existing.setCategoria(producto.getCategoria());
            existing.setPrecio(producto.getPrecio());
            existing.setStock(producto.getStock());
            existing.setDescripcion(producto.getDescripcion());
            existing.setActivo(producto.getActivo());
            // copiar imágenes si vienen en el objeto producto (permitir reemplazo)
            if (producto.getImagen() != null) existing.setImagen(producto.getImagen());
            if (producto.getImagen2() != null) existing.setImagen2(producto.getImagen2());
            if (producto.getImagen3() != null) existing.setImagen3(producto.getImagen3());
            if (producto.getImagen4() != null) existing.setImagen4(producto.getImagen4());
            return productoRepository.save(existing);
        }).orElseGet(() -> {
            producto.setId(id);
            return productoRepository.save(producto);
        });
    }

    public void delete(Long id) {
        productoRepository.deleteById(id);
    }
}
