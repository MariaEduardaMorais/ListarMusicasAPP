package com.ifmg.listarmusicasapp

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ifmg.listarmusicasapp.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItunesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ItunesAdapter()
        recyclerView.adapter = adapter

        binding.searchButton.setOnClickListener {
            val artistName = binding.searchEditText.text.toString()
            if (artistName.isNotEmpty()) {
                searchItunes(artistName)
            }
        }
    }
    private inner class SearchItunesTask : AsyncTask<String, Void, List<ItunesResult>>() {

        override fun doInBackground(vararg params: String): List<ItunesResult> {
            val artistName = params[0]
            val url = URL("https://itunes.apple.com/search?term=$artistName")
            val req: HttpsURLConnection = url.openConnection() as HttpsURLConnection
            val buffer = BufferedReader(InputStreamReader(req.inputStream))

            val response = StringBuilder()
            buffer.forEachLine { response.append(it) }

            return parseJsonResponse(response.toString())
        }

        override fun onPostExecute(results: List<ItunesResult>) {
            // Este método é chamado na thread principal após a conclusão da tarefa em segundo plano
            adapter.setResults(results)
        }
    }

    private fun searchItunes(artistName: String) {
        // Executa a tarefa em segundo plano
        SearchItunesTask().execute(artistName)
    }

    private fun parseJsonResponse(response: String): List<ItunesResult> {
        val results = mutableListOf<ItunesResult>()

        try {
            val jsonObject = JSONObject(response)
            val jsonArray: JSONArray = jsonObject.optJSONArray("results") // Use optJSONArray para lidar com casos em que "results" pode ser nulo

            jsonArray?.let {
                for (i in 0 until it.length()) {
                    val item = it.getJSONObject(i)
                    // Verificar se a chave "trackName" existe antes de tentar acessar seu valor
                    if (item.has("trackName")) {
                        val trackName = item.getString("trackName")
                        val artistName = item.getString("artistName")
                        val collectionName = item.optString("collectionName", null)
                        val releaseDate = item.optString("releaseDate", null)

                        val itunesResult = ItunesResult(trackName, artistName, collectionName, releaseDate)
                        results.add(itunesResult)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return results
    }
}