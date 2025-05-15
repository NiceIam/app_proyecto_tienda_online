package com.example.app_practica.db

import com.example.app_practica.Fragments.Producto

class ProductosProvider {
    companion object{
        val productoList = listOf(
            Producto(
                1, "Samsung Galaxy S22",
                "https://tu360compras.grupobancolombia.com/269183-large_default/celular-samsung-s22-256gb-8g-5g.jpg",
                "Celular",
                899.99,
                949.99,
                2.8f,
                20,
                false,
                "Celular de alta gama con excelente cámara y rendimiento.",
                mapOf("Pantalla" to "6.1'' AMOLED", "RAM" to "8GB", "Almacenamiento" to "128GB")
            ),
            Producto(
                2, "iPhone 14",
                "https://http2.mlstatic.com/D_Q_NP_918579-MLM51559384401_092022-O.webp",
                "Celular",
                999.99,
                1049.99,
                4.9f,
                15,
                true,
                "El último modelo de Apple con chip A15 Bionic.",
                mapOf("Pantalla" to "6.1'' OLED", "RAM" to "6GB", "Almacenamiento" to "256GB")
            ),
            Producto(
                3, "Xiaomi Redmi Note 12",
                "https://refurbi.com.co/cdn/shop/files/Redmi-Note-12.webp?v=1720128164",
                "Celular",
                299.99,
                319.99,
                4.6f,
                50,
                true,
                "Excelente rendimiento a bajo costo.",
                mapOf("Pantalla" to "6.67'' AMOLED", "RAM" to "6GB", "Almacenamiento" to "128GB")
            ),
            Producto(
                4, "MacBook Pro M1",
                "https://musicalboutique.co/cdn/shop/products/1066866337_1.jpg?v=1647323033",
                "Computador",
                1299.99,
                1399.99,
                4.9f,
                10,
                true,
                "Laptop profesional con chip Apple M1.",
                mapOf("Pantalla" to "13.3'' Retina", "RAM" to "8GB", "Almacenamiento" to "512GB SSD")
            ),
            Producto(
                5, "Dell XPS 13",
                "https://m.media-amazon.com/images/I/91MXLpouhoL.jpg",
                "Computador",
                1099.99,
                1149.99,
                4.7f,
                12,
                true,
                "Ultrabook potente y ligero.",
                mapOf("Pantalla" to "13.4'' FHD+", "RAM" to "16GB", "Almacenamiento" to "512GB SSD")
            ),
            Producto(
                6, "Lenovo IdeaPad 3",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSri_I7j9x4f1UZvAzerCrm9oR0QiJhadz1Mw&s",
                "Computador",
                599.99,
                649.99,
                4.4f,
                30,
                true,
                "Portátil económico para estudiantes y trabajo básico.",
                mapOf("Pantalla" to "15.6'' HD", "RAM" to "8GB", "Almacenamiento" to "256GB SSD")
            ),
            Producto(
                7, "Audífonos Bluetooth JBL",
                "https://media.falabella.com/falabellaCO/72724686_1/w=1500,h=1500,fit=pad",
                "Accesorio",
                79.99,
                89.99,
                1.6f,
                100,
                true,
                "Audífonos inalámbricos con buen sonido y duración de batería.",
                mapOf("Duración" to "20 horas", "Cancelación de ruido" to "Sí")
            ),
            Producto(
                8, "Teclado mecánico Logitech",
                "https://http2.mlstatic.com/D_NQ_NP_976755-MLU77807458466_072024-O.webp",
                "Accesorio",
                119.99,
                129.99,
                4.8f,
                40,
                true,
                "Teclado mecánico RGB para gaming o productividad.",
                mapOf("Tipo" to "Mecánico", "Conectividad" to "USB-C")
            ),
            Producto(
                9, "Mouse inalámbrico Logitech MX Master 3",
                "https://http2.mlstatic.com/D_NQ_NP_984979-MLU75050141684_032024-O.webp",
                "Accesorio",
                99.99,
                109.99,
                4.9f,
                25,
                true,
                "Mouse premium con múltiples botones y diseño ergonómico.",
                mapOf("DPI" to "4000", "Conexión" to "Bluetooth / USB receptor")
            ),
            Producto(
                10, "Cargador portátil Anker 20,000mAh",
                "https://http2.mlstatic.com/D_NQ_NP_967265-MLU79236188975_092024-O.webp",
                "Accesorio",
                49.99,
                54.99,
                4.7f,
                60,
                true,
                "Power bank de alta capacidad con carga rápida.",
                mapOf("Capacidad" to "20,000mAh", "Puertos" to "2 USB-A, 1 USB-C")
            )
        )


    }
}