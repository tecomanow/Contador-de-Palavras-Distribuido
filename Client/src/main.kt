import java.nio.file.Files
import java.nio.file.Path
import kotlin.concurrent.thread


fun  main() {

    val serversIps = "files/servers.txt"
    val filePath = "files/teste.zip"

    val zipFilesPaths = arrayListOf("files/teste.zip", "files/teste1.zip", "files/teste2.zip")
    //val occurrences = 0
    //CRIAR UM ARRAY DE FILESPATH E ENVIAR UM PRA CADA SERVIDOR CONECTADO

    val connectedServers : ArrayList<CustomServer.Server> = ArrayList()

    //var serverNumber = 0
    Files.readAllLines(Path.of(serversIps)).forEach { serverAddress ->
        println(serverAddress)
        val serverAndPort = serverAddress.split(":")
        val server = serverAndPort[0].trim()
        val port = serverAndPort[1].trim()
        //println("Server: ${serverAndPort[0]}")
        //println("Port: ${serverAndPort[1]}")
        //val s = Server(server, port.toInt())
        val s = CustomServer.Server(server, port.toInt())
        if(s.connect()){
            connectedServers.add(s)
        }
        //serverNumber++
    }

    println("A conexão foi aceita por: ${connectedServers.size} servidores")

    if(connectedServers.size > 0){
        var count = 0
        connectedServers.forEach {
            it.sendZipFile(Path.of(zipFilesPaths[count]))
            //it.setListOccurrences(occurrences)
            it.start()
            count++
        }
    }

    /*thread {
        do {

        }while (connectedServers.size != occurrences.size)

        var totalOccurrence = 0
        occurrences.forEach {
            var final = it.replace("[","")
            final = final.replace("]", "")
            final = final.trim()
            totalOccurrence += final.toInt()
        }

        println("Occurrence total is: $totalOccurrence in all files")
    }*/


}