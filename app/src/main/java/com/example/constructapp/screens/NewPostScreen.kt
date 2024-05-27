package com.example.constructapp.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constructapp.R
import com.example.constructapp.UserProfileImage

@Composable
fun NewPostScreen() {
//    BackHandler {
//        onBackPressed()
//    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        NewPostTopBar(
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(45.dp))

        Text(text = "Subject", fontSize = 16.sp, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(8.dp))

        var subjectText by remember { mutableStateOf(TextFieldValue("")) }
        BasicTextField(
            value = subjectText,
            onValueChange = { subjectText = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Description", fontSize = 16.sp, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(8.dp))

        var descriptionText by remember { mutableStateOf(TextFieldValue("")) }
        BasicTextField(
            value = descriptionText,
            onValueChange = { descriptionText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.AddBox,
                contentDescription = "Add File",
                modifier = Modifier.clickable { } // Acción para añadir archivo
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { }) {//Acción para cancelar
                Text(text = "CANCEL")
            }
            Button(
                onClick = { }, //Acción para postear
            ) {
                Text(text = "POST IT!", color = Color.White)
            }
        }
    }
}

@Composable
private fun NewPostTopBar(
    modifier: Modifier = Modifier
    //onBackButtonClicked: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(
            onClick = {}, //onBackButtonClicked,
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.detail_topbar_back_button_padding_horizontal))
                .background(MaterialTheme.colorScheme.surface, shape = CircleShape),
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = ""
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "NEW POST",
                color = Color.Blue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        UserProfileImage(
            drawableResource = R.drawable.profile,
            description = "",
            modifier = Modifier
                .padding(end = dimensionResource(R.dimen.topbar_profile_image_padding_end))
                .size(dimensionResource(R.dimen.topbar_profile_image_size))
        )
    }
}

@Composable
@Preview(showBackground = true)
fun NewPostScreenPreview() {
    NewPostScreen()
}