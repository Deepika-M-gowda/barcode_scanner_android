/*
 * Barcode Scanner
 * Copyright (C) 2021  Atharok
 *
 * This file is part of Barcode Scanner.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.atharok.barcodescanner.data.file

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class FileFetcher(private val context: Context) {

    /**
     * Récupère le fichier de la mémoire interne de l'appareil. Si il n'existe pas, on le télécharge.
     */
    fun fetchFile(localFileNameWithExtension: String, webUrl: String): File {

        // On récupère le fichier dans le stockage interne de l'appareil
        var file: File = context.getFileStreamPath(localFileNameWithExtension)

        // Si le fichier n'existe pas dans l'espace de stockage
        if (!file.exists()) {

            try {
                // Télécharge le fichier
                downloadFile(localFileNameWithExtension, webUrl)
                //val successful: Boolean = downloadFile(localFileNameWithExtension, webUrl)
            } catch (e: Exception){
                file.delete()
            }

            //if(!successful)
                //Log.e("FileFetcher","Une erreur s'est produit lors du téléchargement de la dépendance.")

            // On récupère le fichier qui vient d'être enregistré dans la mémoire interne de l'appareil
            file = context.getFileStreamPath(localFileNameWithExtension)

        }

        return file
    }

    /**
     * Télécharge le fichier à partir de l'URL.
     */
    private fun downloadFile(localFileNameWithExtension: String, webUrl: String): Boolean {

        var successful = false

        // On se connecte au serveur distant
        val urlConnector = UrlConnector()
        val input: InputStream? = urlConnector.openConnection(webUrl)

        // On télécharge le fichier que l'on enregistre dans la mémoire interne de l'appareil
        if (input != null) {
            successful = writeIntoFile(input, localFileNameWithExtension)
        }

        // On ferme la connexion avec le serveur distant
        urlConnector.closeConnection()

        return successful
    }

    private fun writeIntoFile(input: InputStream, localFileNameWithExtension: String): Boolean {

        var successful=false

        val localUriStr = "${context.filesDir.absolutePath}/${localFileNameWithExtension}"

        try {
            val output: OutputStream = FileOutputStream(localUriStr)

            val data = ByteArray(1024)

            var count: Int = input.read(data)
            while (count != -1) {
                output.write(data, 0, count) // -> On écrit les données du fichier dans le fichier locale

                count = input.read(data)
            }

            output.flush()
            output.close()
            successful = true

        } catch (e: Exception){
            //Log.e("ERROR", e.message ?: "Erreur lors de l'écriture du fichier")
        }

        return successful
    }
}