# ProgettoTDPPISD
Progetto universitario per la materia Tecniche di programmazione per i sistemi distribuiti.
L'idea è quella di implementare un server in java che restituisce le previsioni meteo (prese tramite API) e un client in python che interroga il server.
Possibili ottimizzazioni: 
- Salvare in un file le città che hanno già avuto un riscontro negativo (esempio non trovate nelle API) in modo da evitare le chiamate.
- Creare un sistema di cache che eviti di fare le chiamate al server se è già stata richiesta quella località in un passato recente (1 ora?).
- Utilizzare un client visivo per python per il client?.