import { useState } from 'react';
import { View, Text, TextInput, Button, ScrollView, StyleSheet, Dimensions} from 'react-native';

export default function MyInputText(){
    const [text, setText] = useState()
    const [enviar, setEnviar] = useState()


    return(
        <View>
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
            <TextInput
                placeholder="Escribe aqui..."
                onChangeText={t=> setText(t)}
            />
            <Button
                title="Enviar"
                onPress={()=>setEnviar(text)}
            />
            </ScrollView>
        </View>
    );
}

const styles = StyleSheet.create({
    input: {
        width:Dimensions.get('window').width,
    },

});