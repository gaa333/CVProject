package com.garam.cvproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.garam.cvproject.ui.theme.CVProjectTheme

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CVProjectTheme {
                ResultScreen()
            }
        }
    }
}

@Composable
fun ResultScreen() {
    val context = LocalContext.current as? Activity
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context?.startActivity(intent)

            }
        ) {
            Text("메인화면으로")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    CVProjectTheme {
        ResultScreen()
    }
}