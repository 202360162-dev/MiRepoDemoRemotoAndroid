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
    fetch('http://202.5.96.57:4000/movies')
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
<View style={styles.card}>
        {item.poster ? (
            <Image source={{uri: item.poster}} style={styles.poster}/>
        ):(
            <View>
                <Text style={styles.noposter}>No Image</Text>
            </View>
        )}
        <View style={styles.info}>
            <Text style={styles.title}>{item.title}</Text>
            <Text style={styles.plot}>{item.fullplot || "Sin descripción"}</Text>
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
  poster: {
    width: 100,
    height: 150,
    resizeMode: 'cover',
  },

  loader: {
    flex: 1,
    justifyContent: "center",
  },
  card: {
    flexDirection: "row",
    padding: 20,
    margin: 10,
    backgroundColor: "#fafaab",
    borderRadius: 10,
  },
  poster: {
    width: 100,
    height: 140,
    borderRadius:10,
  },
  noposter:{
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#ddd"

  },
  info:{
    flex: 1,
    marginLeft: 20,
    
  },
  plot:{
    fontSize:12,
    color: "gray",

  },
  title:{
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 10,
  },

});