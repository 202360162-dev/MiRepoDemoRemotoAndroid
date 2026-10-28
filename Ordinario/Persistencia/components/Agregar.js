import React, { useState } from 'react';
import { View, TextInput, Button, StyleSheet, Alert } from 'react-native';
import { supabase } from '../supabase';

export default function Agregar({ onTerminado }) {
  const [producto, setProducto] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [precio, setPrecio] = useState('');

  const agregarRegistro = async () => {
    if (!producto || !descripcion || !precio) {
      Alert.alert("Error", "Llena todos los campos");
      return;
    }
    const { error } = await supabase
      .from('productos')
      // Se eliminó la tilde en "descripcion" y se cambió a parseInt para coincidir con tu int4[cite: 9]
      .insert([{ producto, descripcion, precio: parseInt(precio, 10) }]);
    
    if (error) Alert.alert("Error", error.message);
    else {
      setProducto(''); setDescripcion(''); setPrecio('');
      onTerminado(); 
    }
  };

  return (
    <View style={styles.container}>
      <TextInput placeholder="Producto" value={producto} onChangeText={setProducto} style={styles.input} />
      <TextInput placeholder="Descripción" value={descripcion} onChangeText={setDescripcion} style={styles.input} />
      <TextInput placeholder="Precio" value={precio} onChangeText={setPrecio} keyboardType="numeric" style={styles.input} />
      <Button title="Agregar Producto" onPress={agregarRegistro} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { marginBottom: 20 },
  input: { borderWidth: 1, borderColor: '#ccc', padding: 10, marginBottom: 10, borderRadius: 5 }
});