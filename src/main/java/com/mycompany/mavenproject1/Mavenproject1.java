package com.mycompany.mavenproject1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class Mavenproject1 extends JFrame {

    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregarProducto;
    private JButton btnQuitarStock;
    private JButton btnEliminarProducto;
    private JButton btnModificarPrecio;
    private JButton btnAgregarStock;
    private JTextField txtNombreProducto;
    private JTextField txtPrecioProducto;
    private JTextField txtCantidadProducto;
    private JTextField txtCantidadQuitar;
    private JTextField txtNuevoPrecioProducto;
    private JTextField txtCantidadAgregar;
    private Connection conn;

    public Mavenproject1() {
        inicializarComponentes();
        conectarBaseDatos();
        cargarProductos();
    }

    private void inicializarComponentes() {
        setTitle("Gestión de Ventas de Ropa y Vestimenta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        setContentPane(panelPrincipal);

        // Panel para la tabla de productos
        JPanel panelTabla = new JPanel(new BorderLayout());
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Precio", "Cantidad"}, 0);
        tablaProductos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        // Panel para agregar productos
        JPanel panelAgregarProducto = new JPanel(new GridLayout(5, 2, 10, 10));
        panelAgregarProducto.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));

        JLabel lblNombre = new JLabel("Nombre:");
        txtNombreProducto = new JTextField();
        JLabel lblPrecio = new JLabel("Precio:");
        txtPrecioProducto = new JTextField();
        JLabel lblCantidad = new JLabel("Cantidad:");
        txtCantidadProducto = new JTextField();
        btnAgregarProducto = new JButton("Agregar");
        btnAgregarProducto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarProducto();
            }
        });

        panelAgregarProducto.add(lblNombre);
        panelAgregarProducto.add(txtNombreProducto);
        panelAgregarProducto.add(lblPrecio);
        panelAgregarProducto.add(txtPrecioProducto);
        panelAgregarProducto.add(lblCantidad);
        panelAgregarProducto.add(txtCantidadProducto);
        panelAgregarProducto.add(btnAgregarProducto);

        // Panel para quitar y agregar stock
        JPanel panelStock = new JPanel(new GridLayout(2, 1, 10, 10));
        panelStock.setBorder(BorderFactory.createTitledBorder("Stock"));

        // Panel para quitar stock
        JPanel panelQuitarStock = new JPanel(new GridLayout(2, 2, 10, 10));
        panelQuitarStock.setBorder(BorderFactory.createTitledBorder("Quitar Stock"));

        JLabel lblCantidadQuitar = new JLabel("Cantidad a quitar:");
        txtCantidadQuitar = new JTextField();
        btnQuitarStock = new JButton("Quitar Stock");
        btnQuitarStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quitarStock();
            }
        });

        panelQuitarStock.add(lblCantidadQuitar);
        panelQuitarStock.add(txtCantidadQuitar);
        panelQuitarStock.add(btnQuitarStock);

        // Panel para agregar stock
        JPanel panelAgregarStock = new JPanel(new GridLayout(2, 2, 10, 10));
        panelAgregarStock.setBorder(BorderFactory.createTitledBorder("Agregar Stock"));

        JLabel lblCantidadAgregar = new JLabel("Cantidad a agregar:");
        txtCantidadAgregar = new JTextField();
        btnAgregarStock = new JButton("Agregar Stock");
        btnAgregarStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarStock();
            }
        });

        panelAgregarStock.add(lblCantidadAgregar);
        panelAgregarStock.add(txtCantidadAgregar);
        panelAgregarStock.add(btnAgregarStock);

        panelStock.add(panelQuitarStock);
        panelStock.add(panelAgregarStock);

        // Panel para eliminar productos
        JPanel panelEliminarProducto = new JPanel(new GridLayout(1, 1, 10, 10));
        panelEliminarProducto.setBorder(BorderFactory.createTitledBorder("Eliminar Producto"));
        btnEliminarProducto = new JButton("Eliminar Producto");
        btnEliminarProducto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarProducto();
            }
        });
        btnEliminarProducto.setPreferredSize(new Dimension(150, 30));
        panelEliminarProducto.add(btnEliminarProducto);

        // Panel para modificar el precio
        JPanel panelModificarPrecio = new JPanel(new GridLayout(3, 2, 10, 10));
        panelModificarPrecio.setBorder(BorderFactory.createTitledBorder("Modificar Precio"));

        JLabel lblNuevoPrecio = new JLabel("Nuevo Precio:");
        txtNuevoPrecioProducto = new JTextField();
        btnModificarPrecio = new JButton("Modificar Precio");
        btnModificarPrecio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarPrecioProducto();
            }
        });

        panelModificarPrecio.add(lblNuevoPrecio);
        panelModificarPrecio.add(txtNuevoPrecioProducto);
        panelModificarPrecio.add(btnModificarPrecio);

        // Agregar paneles al panel principal
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);
        panelPrincipal.add(panelAgregarProducto, BorderLayout.NORTH);
        panelPrincipal.add(panelStock, BorderLayout.SOUTH);
        panelPrincipal.add(panelEliminarProducto, BorderLayout.EAST);
        panelPrincipal.add(panelModificarPrecio, BorderLayout.WEST);
    }

    private void conectarBaseDatos() {
        try {
            // URL de la base de datos (con el prefijo jdbc:mysql://)
            String url = "jdbc:mysql://localhost:3306/ventas";
            String usuario = "root"; // Reemplaza con tu usuario de MySQL
            String contraseña = "root"; // Reemplaza con tu contraseña de MySQL
            conn = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Salir de la aplicación en caso de error
        }
    }

    private void cargarProductos() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id_producto, nombre, precio, cantidad FROM productos")) {
            // Limpiar tabla antes de cargar datos
            modeloTabla.setRowCount(0);
            // Llenar tabla con datos de la base de datos
            while (rs.next()) {
                Vector<Object> fila = new Vector<>();
                fila.add(rs.getInt("id_producto"));
                fila.add(rs.getString("nombre"));
                fila.add(rs.getDouble("precio"));
                fila.add(rs.getInt("cantidad"));
                modeloTabla.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarProducto() {
        String nombre = txtNombreProducto.getText();
        double precio = Double.parseDouble(txtPrecioProducto.getText());
        int cantidad = Integer.parseInt(txtCantidadProducto.getText());

        String sql = "INSERT INTO productos (nombre, precio, cantidad) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setDouble(2, precio);
            pstmt.setInt(3, cantidad);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Producto agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarProductos(); // Actualizar tabla después de agregar producto
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarStock() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProducto = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        int cantidadActual = (int) modeloTabla.getValueAt(filaSeleccionada, 3);
        int cantidadQuitar = Integer.parseInt(txtCantidadQuitar.getText());

        if (cantidadQuitar > cantidadActual) {
            JOptionPane.showMessageDialog(this, "La cantidad a quitar excede el stock actual.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE productos SET cantidad = cantidad - ? WHERE id_producto = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidadQuitar);
            pstmt.setInt(2, idProducto);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Stock actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarProductos(); // Actualizar tabla después de quitar stock
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar stock: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reiniciarAutoincremento() {
        String sql = "ALTER TABLE productos AUTO_INCREMENT = 1";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al reiniciar autoincremento: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProducto = (int) modeloTabla.getValueAt(filaSeleccionada, 0);

        String sql = "DELETE FROM productos WHERE id_producto = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            reiniciarAutoincremento(); // Reiniciar autoincremento después de eliminar todos los productos
            cargarProductos(); // Actualizar tabla después de eliminar producto
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarPrecioProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProducto = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        double nuevoPrecio;
        try {
            nuevoPrecio = Double.parseDouble(txtNuevoPrecioProducto.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa un precio válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE productos SET precio = ? WHERE id_producto = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, nuevoPrecio);
            pstmt.setInt(2, idProducto);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Precio actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarProductos(); // Actualizar tabla después de modificar el precio
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el precio: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarStock() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProducto = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        int cantidadAgregar = Integer.parseInt(txtCantidadAgregar.getText());

        String sql = "UPDATE productos SET cantidad = cantidad + ? WHERE id_producto = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidadAgregar);
            pstmt.setInt(2, idProducto);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Stock agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarProductos(); // Actualizar tabla después de agregar stock
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar stock: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        //Ejecuta la app
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Mavenproject1().setVisible(true);
            }
        });
    }
}