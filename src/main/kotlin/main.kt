import callback.OnCompletedCalculatorListener
import server.Server
import watch.WatchFolder
import java.io.*
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.concurrent.thread


fun main(args: Array<String>) {

    val server : ServerSocket = ServerSocket(Server.SERVER_PORT_3)
    println("Waiting connection")
    val socket = server.accept();
    println("The client has connected successfully")

    val destPath = "files/${Server.SERVER_PORT_3}/zip"

    try {
        val objectAsByte = ByteArray(socket.receiveBufferSize)
        val bf = BufferedInputStream(socket.getInputStream())
        bf.read(objectAsByte)

        val file: CustomFile = getObjectFromByte(objectAsByte) as CustomFile

        val dir = destPath + File.separator + file.name;
        val tempFile = File(destPath)
        if(!tempFile.exists()){
            println(tempFile.absolutePath)
            tempFile.mkdirs()
        }
        println("Writing file at $dir")

        val fos = FileOutputStream(dir)
        fos.write(file.content)
        fos.close()
        println("All file was successfully written")
        unzipAndRunProgram(socket, file.name)
    }catch (e: Exception){
        e.printStackTrace()
        println("Something went wrong")
    }

}

private fun getObjectFromByte(objectAsByte: ByteArray): Any? {
    var obj: Any? = null
    var bis: ByteArrayInputStream? = null
    var ois: ObjectInputStream? = null
    try {
        bis = ByteArrayInputStream(objectAsByte)
        ois = ObjectInputStream(bis)
        obj = ois.readObject()
        bis.close()
        ois.close()
    } catch (e: IOException) {
        println("Something went wrong with the file")
        e.printStackTrace()
    } catch (e: ClassNotFoundException) {
        println("Class not found")
        e.printStackTrace()
    }
    return obj
}

fun unzipAndRunProgram(socket: Socket, fileName: String) {
    val filePath = "files/${Server.SERVER_PORT_3}/zip/${fileName}"
    val destPath = "files/${Server.SERVER_PORT_3}/output_zip_files"
    val destPathResult = destPath + File.separator + "results"

    if (!File(destPathResult).exists()) {
        File(destPathResult).mkdirs()
    }

    val wf = WatchFolder(object : OnCompletedCalculatorListener {
        override fun onComplete(path: Path) {
            println(Files.readString(path))
            try {
                val resultFile = File(path.toUri())

                val bf: BufferedOutputStream = BufferedOutputStream(socket.getOutputStream())
                val bFile = ByteArray(resultFile.length().toInt())
                val fis: FileInputStream = FileInputStream(resultFile)
                fis.read(bFile)
                fis.close()

                val fileToSend = CustomFile(
                    resultFile.name,
                    bFile,
                    "",
                    Date().toString()
                )

                sendFileToClient(bf, fileToSend)
            }catch (e: Exception){
                e.printStackTrace()
                println("Something went wrong sending file back to the client")
            }
        }
    })

    thread {
        wf.watchFolder(destPathResult)
    }
    /*val executor: ExecutorService = Executors.newCachedThreadPool()
    executor.submit(WatchCallable(destPathResult))*/

    //Thread.sleep(2000)

    val unzipUtils = UnzipUtils()
    unzipUtils.unzip(filePath, destPath)

    //val builder: ProcessBuilder()
    val p: Process = Runtime.getRuntime().exec("cmd /c cd ${File(destPath).absolutePath} & program.exe")
    //println("cmd ${File(destPath).absolutePath}")
}

private fun sendFileToClient(bf: BufferedOutputStream?, fileToSend: CustomFile) {
    val bytea: ByteArray? = serializeFile(fileToSend)
    bytea?.let {
        bf!!.write(it)
        bf.flush()
        //bf!!.close()
    }

    //socket!!.close()
}

private fun serializeFile(fileToSend: CustomFile): ByteArray? {
    try {
        val bao = ByteArrayOutputStream()
        val ous: ObjectOutputStream = ObjectOutputStream(bao)
        ous.writeObject(fileToSend)
        return bao.toByteArray()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return null
}


