import java.nio.file.Files
import java.nio.file.Path
import kotlin.concurrent.thread


fun  main() {

    val serversIps = "files/servers.txt"
    val zipFilesPaths = arrayListOf("files/teste.zip", "files/teste1.zip", "files/teste2.zip")
    val connectedServers : ArrayList<CustomServer.Server> = ArrayList()

    Files.readAllLines(Path.of(serversIps)).forEach { serverAddress ->
        println(serverAddress)
        val serverAndPort = serverAddress.split(":")
        val server = serverAndPort[0].trim()
        val port = serverAndPort[1].trim()

        val s = CustomServer.Server(server, port.toInt())
        if (s.connect()) {
            connectedServers.add(s)
        }

    }

    println("The connections was accepted by: ${connectedServers.size} servers")

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