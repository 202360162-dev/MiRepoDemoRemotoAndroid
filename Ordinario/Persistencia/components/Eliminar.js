import React from 'react';
import { Button, Alert } from 'react-native';
import { supabase } from '../supabase';

export default function Eliminar({ id, onTerminado }) {
  const eliminarRegistro = async () => {
    const { error } = await supabase.from('productos').delete().eq('id', id);
    if (error) Alert.alert("Error al eliminar", error.message);
    else onTerminado(); // Avisa al padre que recargue la lista
  };

  return <Button title="Eliminar" onPress={eliminarRegistro} color="red" />;
}