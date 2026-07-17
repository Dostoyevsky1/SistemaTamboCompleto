// Comunicación: Define la URL base de la API REST que consume el frontend, adaptándose automáticamente si corre en desarrollo (localhost) o producción.
import { isDevMode } from '@angular/core';

export const API_BASE_URL = isDevMode() 
  ? 'http://localhost:8080' 
  : 'https://sistema-tambo-backend-rx0c.onrender.com'; // Modificable por el dominio final asignado en el despliegue.
