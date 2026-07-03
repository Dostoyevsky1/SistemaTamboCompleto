// Comunicación: Archivo de entrada de Angular que arranca AppModule. El refresco automático de la interfaz es gestionado nativamente por provideZonelessChangeDetection() en AppModule.
import { platformBrowser } from '@angular/platform-browser';
import { AppModule } from './app/app.module';

platformBrowser().bootstrapModule(AppModule)
  .catch(err => console.error(err));
