import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Alert } from 'react-native';
import { supabase } from './supabase';
import Agregar from './components/Agregar';
import Consultar from './components/Consultar';
import Actualizar from './components/Actualizar';

export default function App() {
  const [productos, setProductos] = useState([]);
  const [productoAEditar, setProductoAEditar] = useState(null);

  const consultarRegistros = async () => {
    const { data, error } = await supabase.from('productos').select('*');
    if (error) Alert.alert("Error de consulta", error.message);
    else setProductos(data);
  };

  useEffect(() => {
    consultarRegistros();
  }, []);

  const resetearVista = () => {
    setProductoAEditar(null);
    consultarRegistros();
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Gestión de Productos</Text>
      
      {productoAEditar ? (
        <Actualizar productoAEditar={productoAEditar} onTerminado={resetearVista} />
      ) : (
        <Agregar onTerminado={consultarRegistros} />
      )}

      <Consultar 
        productos={productos} 
        onEditar={setProductoAEditar} 
        onTerminado={consultarRegistros} 
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, marginTop: 40 },
  title: { fontSize: 20, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' }
});