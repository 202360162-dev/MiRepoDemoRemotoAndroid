import { useState } from 'react';
import { View, Text, TextInput, Button, ScrollView, StyleSheet, Dimensions} from 'react-native';
import MyInputText from './componentes/DemoInputText';
import FlagComponent from './componentes/FlagComponent';



export default function App() {
    const [text, setText] = useState()
    const [enviar, setEnviar] = useState()
  return (
    <View style={styles.container}>
      <View style={styles.panel1}>

      </View>
      <View style={styles.panel2}>
        <ScrollView style={styles.input}>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
           
            </ScrollView>
      </View>
      <View style={styles.panel3}>
         <TextInput
                placeholder="Escribe aqui..."
                onChangeText={t=> setText(t)}
            />
         <Button
                title="Enviar"
                onPress={()=>setEnviar(text)}
            />
      </View>
    </View>

  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#45d499',
  },
  panel1: {
    flex: 1,
    backgroundColor: '#ccc50d',
  },
  panel2: {
    flex: 1,
    backgroundColor: 'rgb(255, 170, 252)',
  },
  panel3: {
    flex: 1,
    backgroundColor: '#3cfa07',
  },
});
