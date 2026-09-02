import { StatusBar } from 'expo-status-bar';

import { View, Text } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import HomeScreen from './componentes/HomeScreen';
import SearchScreen from './componentes/SearchScreen';
import ProfileScreen from './componentes/ProfileScreen';

const Tab = createBottomTabNavigator();



export default function App() {
  return (
    <NavigationContainer>
      <Tab.Navigator>
        <Tab.Screen name="Inicio" component={HomeScreen} />
        <Tab.Screen name="Buscar" component={SearchScreen} />
        <Tab.Screen name="Perfil" component={ProfileScreen} />
      </Tab.Navigator>
    </NavigationContainer>
  );
}
