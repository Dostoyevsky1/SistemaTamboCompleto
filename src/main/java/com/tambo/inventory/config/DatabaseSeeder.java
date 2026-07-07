package com.tambo.inventory.config;

import com.tambo.inventory.entity.Producto;
import com.tambo.inventory.entity.Sucursal;
import com.tambo.inventory.repository.ProductoRepository;
import com.tambo.inventory.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public DatabaseSeeder(SucursalRepository sucursalRepository, ProductoRepository productoRepository) {
        this.sucursalRepository = sucursalRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (sucursalRepository.count() == 0) {
            Sucursal s1 = new Sucursal(null, "Tambo - Miraflores", "Av. Larco 456");
            Sucursal s2 = new Sucursal(null, "Tambo - San Isidro", "Av. Javier Prado 120");
            Sucursal s3 = new Sucursal(null, "Tambo - Surco", "Av. Primavera 789");

            sucursalRepository.save(s1);
            sucursalRepository.save(s2);
            sucursalRepository.save(s3);
        }

        List<Producto> productos = productoRepository.findAll();
        boolean modified = false;
        
        if (!productos.isEmpty()) {
            Sucursal sucursalPredeterminada = sucursalRepository.findAll().get(0);
            for (Producto p : productos) {
                if (p.getSucursal() == null) {
                    p.setSucursal(sucursalPredeterminada);
                    productoRepository.save(p);
                    modified = true;
                }
            }
        }
    }
}
