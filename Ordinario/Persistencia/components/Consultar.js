import React from 'react';
import { FlatList, View, Text, Button, StyleSheet } from 'react-native';
import Eliminar from './Eliminar';

export default function Consultar({ productos, onEditar, onTerminado }) {
  return (
    <FlatList
      data={productos}
      keyExtractor={(item) => item.id.toString()}
      renderItem={({ item }) => (
        <View style={styles.item}>
          <Text style={styles.itemText}>{item.producto} - ${item.precio}</Text>
          {/* Se eliminó la tilde al mostrar la descripción */}
          <Text>{item.descripcion}</Text>
          <View style={styles.buttons}>
            <Button title="Editar" onPress={() => onEditar(item)} />
            <Eliminar id={item.id} onTerminado={onTerminado} />
          </View>
        </View>
      )}
    />
  );
}

const styles = StyleSheet.create({
  item: { padding: 15, borderBottomWidth: 1, borderColor: '#eee', marginBottom: 5 },
  itemText: { fontWeight: 'bold', fontSize: 16 },
  buttons: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 10 }
});