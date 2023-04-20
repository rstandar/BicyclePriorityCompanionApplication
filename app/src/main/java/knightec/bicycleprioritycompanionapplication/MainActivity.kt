package knightec.bicycleprioritycompanionapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import knightec.bicycleprioritycompanionapplication.ui.theme.BicyclePriorityCompanionApplicationTheme
import org.json.JSONObject


class MainActivity : ComponentActivity() {
    private var status = mutableStateOf("Red")
    private var time = mutableStateOf("1000")
    private val url = "https://tubei213og.execute-api.eu-north-1.amazonaws.com/"
    private val pollHandler = Handler(Looper.getMainLooper())
    private var running : Boolean = true
    private lateinit var queue : RequestQueue
    private val poll = object: Runnable {
        override fun run(){
            requestTrafficLightStatus()
            pollHandler.postDelayed(this, 1000)
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startPolling()
        queue = Volley.newRequestQueue(applicationContext)
        setContent {
            BicyclePriorityCompanionApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Current status: " +status.value)
                        Text(text = "Time to change: " + time.value)
                        Button(
                            onClick = {updatePoll()}
                        ){
                            Text(text = "Toggle updates")
                        }
                    }
                }
            }
        }
    }
    private fun updatePoll(){
        if(running) stopPolling()
        else startPolling()
        running = !running
    }

    private fun startPolling(){
        pollHandler.post(poll)
    }

    private fun stopPolling(){
        pollHandler.removeCallbacks(poll)
    }

    private fun requestTrafficLightStatus(){
        val reqBody = JSONObject()
        reqBody.put("lat", "Debug")
        reqBody.put("lon", "0")
        reqBody.put("prev_lat", "0")
        reqBody.put("prev_lon", "0")

        val req = JsonObjectRequest(
            Request.Method.POST, url, reqBody,
            { response ->
                run {
                    time.value = response["time_left"].toString()
                    status.value = response["status"] as String
                }
            },
            { error ->
                print("ERROR: $error")
            }
        )
        queue.add(req)
    }

}

