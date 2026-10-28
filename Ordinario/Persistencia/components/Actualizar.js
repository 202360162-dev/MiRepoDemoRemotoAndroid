import React, { useState, useEffect } from 'react';
import { View, TextInput, Button, StyleSheet, Alert } from 'react-native';
import { supabase } from '../supabase';

export default function Actualizar({ productoAEditar, onTerminado }) {
  const [producto, setProducto] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [precio, setPrecio] = useState('');

  useEffect(() => {
    setProducto(productoAEditar.producto);
    // Se eliminó la tilde al leer la propiedad del objeto
    setDescripcion(productoAEditar.descripcion);
    setPrecio(productoAEditar.precio.toString());
  }, [productoAEditar]);

  const actualizarRegistro = async () => {
    const { error } = await supabase
      .from('productos')
      // Se eliminó la tilde en "descripcion" y se cambió a parseInt para coincidir con tu int4[cite: 9]
      .update({ producto, descripcion, precio: parseInt(precio, 10) })
      .eq('id', productoAEditar.id);
      
    if (error) Alert.alert("Error", error.message);
    else onTerminado(); 
  };

  return (
    <View style={styles.container}>
      <TextInput placeholder="Producto" value={producto} onChangeText={setProducto} style={styles.input} />
      <TextInput placeholder="Descripción" value={descripcion} onChangeText={setDescripcion} style={styles.input} />
      <TextInput placeholder="Precio" value={precio} onChangeText={setPrecio} keyboardType="numeric" style={styles.input} />
      <Button title="Actualizar Producto" onPress={actualizarRegistro} color="orange" />
      <View style={{ marginTop: 10 }}>
        <Button title="Cancelar" onPress={onTerminado} color="gray" />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { marginBottom: 20 },
  input: { borderWidth: 1, borderColor: '#ccc', padding: 10, marginBottom: 10, borderRadius: 5 }
});