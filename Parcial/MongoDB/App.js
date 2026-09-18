import React, { useState, useEffect } from 'react';
import {
  ActivityIndicator,
  FlatList,
  Image,
  StyleSheet,
  Text,
  View,
} from 'react-native';

export default function App() {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('http://localhost:4000/movies')
      .then((res) => res.json())
      .then((data) => {
        setMovies(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error(error);
        setLoading(false);
      });
  }, []);

  if (loading){
    return(
      <View>
        <ActivityIndicator size="large" color="#0446ed"/>
      </View>
    )
  }

  const renderItem = ({item}) => (
    <View>
      {item.poster ? (
        <Image source={{ uri: item.poster }} style={styles.poster} />
      ):(
        <View>
          <Text>No Image</Text>
        </View>
      )}
      <View>
        <Text>{item.title} </Text>
        <Text>{item.fullplot || "Sin descripcion"} </Text>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <FlatList
        data={movies}
        keyExtractor={(item, index) => String(item._id || item.id || index)}
        renderItem={renderItem}
      />
    </View>
  );

  return (
    <FlatList 
    data={movies}
    keyExtractor={(item)=>item._id}
    renderItem={renderItem}
    />
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    width: '100%',
    padding: 16,
  },
  poster: {
    width: 100,
    height: 150,
    resizeMode: 'cover',
  },
});