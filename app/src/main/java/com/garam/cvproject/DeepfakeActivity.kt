package com.garam.cvproject

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.garam.cvproject.ui.theme.CVProjectTheme
import java.nio.FloatBuffer

class DeepfakeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CVProjectTheme {
                DeepfakeScreen()
            }
        }
    }
}

@Composable
fun DeepfakeScreen() {
    val context = LocalContext.current as? Activity
    var imageURI by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf("") }
    var textFieldValue by remember { mutableStateOf("") }
    var showTextField by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("결과: 없음") } // 결과값 표시를 위한 상태

    val env = OrtEnvironment.getEnvironment()
    val session: OrtSession =
        env.createSession(context!!.assets.open("deepfake_binary_s128_e5_early.onnx").readBytes())

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                imageURI = uri
                imageUrl = ""
            }
        }

    // 배경 색상
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF404040), Color(0xFFBFBFBF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 상단 타이틀
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                "Deepfake Screen",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.shadow(8.dp)
            )
            Spacer(modifier = Modifier.weight(0.05f))

            // 이미지 컨테이너
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.Transparent)
                    .border(2.dp, Color.White, RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imageURI != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageURI),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Image from URL",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("image", fontSize = 20.sp, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            // 결과 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f)
                    .background(Color.Gray)
                    .clip(RoundedCornerShape(15.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .fillMaxHeight()
                            .background(color = Color.Yellow)
                    ) {
                        Text("크롭된 이미지")
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(color = Color.Green),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(resultText, color = Color.White, fontSize = 20.sp)
                    }
                }
//                Text("result", color = Color.White, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.weight(0.05f))

            // 버튼 Row (이미지 선택 및 이미지 주소 입력)
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 이미지 선택 버튼
                Button(
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .shadow(4.dp, RoundedCornerShape(15.dp)),
                    onClick = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                ) {
                    Text("이미지 선택", fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 이미지 주소 입력 버튼
                Button(
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .shadow(4.dp, RoundedCornerShape(15.dp)),
                    onClick = { showTextField = true }
                ) {
                    Text("이미지 주소 입력", fontSize = 15.sp)
                }
            }
            Spacer(modifier = Modifier.weight(0.02f))

            // 이미지 분석 버튼
            Button(
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                enabled = imageURI != null || imageUrl.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
                    .shadow(4.dp, RoundedCornerShape(15.dp)),
                onClick = {
                    // 분석 로직

                    // 분석 로직
                    val bitmap = if (imageURI != null) {
                        val inputStream = context?.contentResolver?.openInputStream(imageURI!!)
                        BitmapFactory.decodeStream(inputStream)
                    } else if (imageUrl.isNotBlank()) {
                        try {
                            val inputStream = java.net.URL(imageUrl).openStream()
                            BitmapFactory.decodeStream(inputStream)
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    }

                    bitmap?.let {
                        try {
                            // 이미지 전처리
                            val inputTensor = preprocessImageForOnnx(it, env)

                            // ONNX 모델 추론
                            val output = session.run(mapOf("input" to inputTensor))
                            val resultArray = (output[0].value as Array<FloatArray>)[0]
                            val maxIndex =
                                resultArray.indices.maxByOrNull { idx -> resultArray[idx] }
                                    ?: -1
                            resultText = "예측 결과: 클래스 $maxIndex"
                        } catch (e: Exception) {
                            resultText = "예측 중 오류 발생: ${e.message}"
                        }
                    } ?: run {
                        resultText = "이미지를 처리할 수 없습니다."
                    }

                }
            ) {
                Text("이미지 분석", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.weight(0.02f))

            // 뒤로가기 버튼
            Button(
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
                    .shadow(4.dp, RoundedCornerShape(15.dp)),
                onClick = { context?.finish() }
            ) {
                Text("뒤로가기", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.weight(0.05f))
        }

        // 텍스트 입력 팝업
        if (showTextField) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        label = { Text("Enter Image URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = { showTextField = false }) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            imageUrl = textFieldValue
                            imageURI = null
                            showTextField = false
                        }) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }
}

/**
 * ONNX 전용 이미지 전처리 함수 (예: 128x128 크기 변환, RGB 정규화)
 */
private fun preprocessImageForOnnx(bitmap: Bitmap, env: OrtEnvironment): OnnxTensor {
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true)
    val floatBuffer = FloatBuffer.allocate(1 * 3 * 128 * 128)

    val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
    val std = floatArrayOf(0.229f, 0.224f, 0.225f)

    val pixels = IntArray(128 * 128)
    resizedBitmap.getPixels(pixels, 0, 128, 0, 0, 128, 128)

    for (y in 0 until 128) {
        for (x in 0 until 128) {
            val pixel = pixels[y * 128 + x]
            val r = ((pixel shr 16 and 0xFF) / 255.0f - mean[0]) / std[0]
            val g = ((pixel shr 8 and 0xFF) / 255.0f - mean[1]) / std[1]
            val b = ((pixel and 0xFF) / 255.0f - mean[2]) / std[2]
            floatBuffer.put(b)
            floatBuffer.put(g)
            floatBuffer.put(r)
        }
    }

    floatBuffer.rewind()
    return OnnxTensor.createTensor(env, floatBuffer, longArrayOf(1, 3, 128, 128))
}


@Preview(showBackground = true)
@Composable
fun DeepfakeScreenPreview() {
    CVProjectTheme {
        DeepfakeScreen()
    }
}