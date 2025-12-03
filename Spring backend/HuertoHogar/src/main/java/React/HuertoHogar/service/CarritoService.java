package React.HuertoHogar.service;

import React.HuertoHogar.model.*;
import React.HuertoHogar.repository.CarritoRepository;
import React.HuertoHogar.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VentaService ventaService;

    public CarritoService(CarritoRepository carritoRepository, UsuarioRepository usuarioRepository, VentaService ventaService) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.ventaService = ventaService;
    }

    public Carrito getOrCreateCart(Long usuarioId) {
        Optional<Carrito> opt = carritoRepository.findByUsuarioId(usuarioId);
        if (opt.isPresent()) return opt.get();

        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Carrito c = new Carrito();
        c.setUsuario(u);
        return carritoRepository.save(c);
    }

    public Carrito addItem(Long usuarioId, CarritoItem item) {
        Carrito c = getOrCreateCart(usuarioId);
        item.setCarrito(c);
        // si existe el mismo producto, incrementar cantidad
        for (CarritoItem ci : c.getItems()) {
            if (ci.getProductoId() != null && ci.getProductoId().equals(item.getProductoId())) {
                int nueva = ci.getCantidad() + (item.getCantidad() == null ? 0 : item.getCantidad());
                if (nueva <= 0) {
                    // eliminar el item si la nueva cantidad es 0 o negativa
                    c.getItems().remove(ci);
                    return carritoRepository.save(c);
                } else {
                    ci.setCantidad(nueva);
                    return carritoRepository.save(c);
                }
            }
        }
        // si la cantidad a agregar es <= 0, no agregar nuevo item
        if (item.getCantidad() == null || item.getCantidad() <= 0) {
            return carritoRepository.save(c);
        }
        c.getItems().add(item);
        return carritoRepository.save(c);
    }

    public Carrito clearCart(Long usuarioId) {
        Carrito c = getOrCreateCart(usuarioId);
        c.getItems().clear();
        return carritoRepository.save(c);
    }

    public void removeItem(Long usuarioId, Long itemId) {
        Carrito c = getOrCreateCart(usuarioId);
        c.getItems().removeIf(i -> i.getId().equals(itemId));
        carritoRepository.save(c);
    }

    public Venta checkout(Long usuarioId) {
        Carrito c = getOrCreateCart(usuarioId);
        Venta v = new Venta();
        v.setUsuario(c.getUsuario());
        for (CarritoItem ci : c.getItems()) {
            VentaItem vi = new VentaItem();
            // productoId in VentaItem is a String now; convertir desde Long
            vi.setProductoId(ci.getProductoId() == null ? "" : String.valueOf(ci.getProductoId()));
            vi.setNombre(ci.getNombre());
            vi.setPrecio(ci.getPrecio());
            vi.setCantidad(ci.getCantidad());
            v.getItems().add(vi);
        }
        Venta saved = ventaService.save(v);
        // limpiar carrito
        c.getItems().clear();
        carritoRepository.save(c);
        return saved;
    }

    @Transactional
    public Venta checkoutWithItems(Long usuarioId, java.util.List<React.HuertoHogar.dto.VentaItemDTO> items) {
        System.out.println("[CarritoService] checkoutWithItems called for usuarioId=" + usuarioId + ", itemsCount=" + (items==null?0:items.size()));
        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Venta v = new Venta();
        v.setUsuario(u);

        if (items != null) {
            for (React.HuertoHogar.dto.VentaItemDTO ci : items) {
                VentaItem vi = new VentaItem();
                // asegurarse de que campos no nulos; productoId almacenado como String
                vi.setProductoId(ci.getProductoId() == null ? "" : ci.getProductoId());
                vi.setNombre(ci.getNombre() == null ? "" : ci.getNombre());
                vi.setPrecio(ci.getPrecio() == null ? 0.0 : ci.getPrecio());
                vi.setCantidad(ci.getCantidad() == null ? 1 : ci.getCantidad());
                vi.setVenta(v);
                v.getItems().add(vi);
            }
        }

        Venta saved = ventaService.save(v);
        // Limpiar el carrito persistente del usuario si existe
        try {
            carritoRepository.findByUsuarioId(usuarioId).ifPresent(c -> {
                c.getItems().clear();
                carritoRepository.save(c);
            });
        } catch (Exception ex) {
            // no detener el proceso de venta si la limpieza del carrito falla, pero loguear
            System.err.println("[CarritoService] error limpiando carrito persistente: " + ex.getMessage());
        }

        return saved;
    }
}
