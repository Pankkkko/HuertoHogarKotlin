package React.HuertoHogar.controller;

import React.HuertoHogar.model.Carrito;
import React.HuertoHogar.model.CarritoItem;
import React.HuertoHogar.dto.VentaItemDTO;
import React.HuertoHogar.model.Venta;
import React.HuertoHogar.service.CarritoService;
import org.springframework.http.ResponseEntity;
import React.HuertoHogar.exception.InsufficientStockException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "http://localhost:5173")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Carrito> getCart(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(carritoService.getOrCreateCart(usuarioId));
        } catch (RuntimeException ex) {
            // Por ejemplo: Usuario no encontrado
            return ResponseEntity.status(404).body(null);
        }
    }

    @PostMapping("/{usuarioId}/items")
    public ResponseEntity<Carrito> addItem(@PathVariable Long usuarioId, @RequestBody CarritoItem item) {
        try {
            return ResponseEntity.ok(carritoService.addItem(usuarioId, item));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/{usuarioId}/items/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long usuarioId, @PathVariable Long itemId) {
        try {
            carritoService.removeItem(usuarioId, itemId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/{usuarioId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable Long usuarioId, @RequestBody(required = false) java.util.List<VentaItemDTO> items) {
        try {
            Venta v;
            if (items != null && !items.isEmpty()) {
                v = carritoService.checkoutWithItems(usuarioId, items);
            } else {
                v = carritoService.checkout(usuarioId);
            }
            return ResponseEntity.ok(v);
        } catch (InsufficientStockException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(400).body(java.util.Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("error", ex.getMessage()));
        }
    }
}
