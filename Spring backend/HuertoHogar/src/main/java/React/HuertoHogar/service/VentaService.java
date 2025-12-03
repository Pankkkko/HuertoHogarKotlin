package React.HuertoHogar.service;

import React.HuertoHogar.model.Venta;
import React.HuertoHogar.model.VentaItem;
import React.HuertoHogar.repository.ProductoRepository;
import React.HuertoHogar.repository.VentaRepository;
import React.HuertoHogar.exception.InsufficientStockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Transactional
    public Venta save(Venta venta) {
        // asegurarse de que cada item tenga referencia a la venta
        if (venta.getItems() != null) {
            for (VentaItem item : venta.getItems()) {
                item.setVenta(venta);
            }
        }
        // calcular total
        double total = 0.0;
        if (venta.getItems() != null) {
            for (VentaItem item : venta.getItems()) {
                total += item.getPrecio() * item.getCantidad();
            }
        }
        venta.setTotal(total);

        // Validar stock disponible antes de guardar
        if (venta.getItems() != null) {
            for (VentaItem item : venta.getItems()) {
                String pid = item.getProductoId();
                if (pid == null || pid.isEmpty()) continue;
                try {
                    Long prodId = Long.parseLong(pid);
                    productoRepository.findById(prodId).ifPresentOrElse(p -> {
                        int stock = p.getStock() == null ? 0 : p.getStock();
                        int qty = item.getCantidad() == null ? 0 : item.getCantidad();
                        if (qty > stock) {
                            throw new InsufficientStockException("Stock insuficiente para producto '" + p.getNombre() + "' (id=" + prodId + "): disponible=" + stock + ", requerido=" + qty);
                        }
                    }, () -> {
                        // producto no encontrado, lanzar excepción
                        throw new InsufficientStockException("Producto no encontrado para id=" + pid);
                    });
                } catch (NumberFormatException nfe) {
                    // pid no numérico: no podemos validar stock
                    System.err.println("[VentaService] productoId no numérico (no se valida stock): " + pid);
                }
            }
        }

        // Persistir la venta primero para obtener ids si es necesario
        Venta saved = ventaRepository.save(venta);

        // Actualizar stock de productos relacionados
        if (saved.getItems() != null) {
            for (VentaItem item : saved.getItems()) {
                String pid = item.getProductoId();
                if (pid == null || pid.isEmpty()) continue;
                try {
                    Long prodId = Long.parseLong(pid);
                    productoRepository.findById(prodId).ifPresent(p -> {
                        int newStock = (p.getStock() == null ? 0 : p.getStock()) - (item.getCantidad() == null ? 0 : item.getCantidad());
                        if (newStock < 0) newStock = 0;
                        p.setStock(newStock);
                        productoRepository.save(p);
                    });
                } catch (NumberFormatException nfe) {
                    // pid no numérico: no podemos actualizar stock
                    System.err.println("[VentaService] productoId no numérico: " + pid);
                }
            }
        }

        return saved;
    }

    public void delete(Long id) { ventaRepository.deleteById(id); }
}
