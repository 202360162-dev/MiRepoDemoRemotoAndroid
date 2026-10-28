import 'react-native-url-polyfill/auto';
import { createClient } from '@supabase/supabase-js';

// URL obtenida de tu Project ID[cite: 5]
const supabaseUrl = 'https://rwtdgplpgibutznnpsah.supabase.co'; 
// Publishable key obtenida de tus configuraciones API[cite: 4]
const supabaseKey = 'sb_publishable_DtxuHia0PGCrQqU8NGfQeg_c2nitnTf'; 

export const supabase = createClient(supabaseUrl, supabaseKey);