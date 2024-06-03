package com.example.constructapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constructapp.R


@Composable
fun ChatsScreen(
    constructAppUiState: DashboardViewState,
    onTabPressed: ((DashboardTab) -> Unit),
    //navigationItemContentList: List<DashboardTabInfo>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                ConversationsTopBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.topbar_padding_vertical))
                )
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

//            Aquí se cargarían todos los chats
//            items(chatList) { chatData ->
//                ChatListItem(chatData)
//            }
        }
//        BottomNavigationBar(
//            currentTab = constructAppUiState.currentMailbox,
//            onTabPressed = onTabPressed,
//            navigationItemContentList = navigationItemContentList,
//            modifier = Modifier
//                .fillMaxWidth()
//        )
    }
}

@Composable
fun ChatListItem(/*chatData: ChatListDataObject*/) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        UserImage()
        UserDetails()
    }
}

@Composable
fun UserImage() {
    Icon(
        modifier = Modifier.size(60.dp),
        painter = painterResource(R.drawable.profile),
        contentDescription = ""
    )
}

@Composable
fun UserDetails(/*chatData: ChatListDataObject*/) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(start = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        MessageHeader()
        MessageSubSection()
    }
}

@Composable
fun MessageHeader(/*chatData: ChatListDataObject*/) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextComponent(
            modifier = Modifier.weight(1f),
            value = "Pepe",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )
        TextComponent(
            value = "12/02/2024",
            fontSize = 11.sp,
            color = /*if ((chatData.message.unreadCount ?: 0) > 0)*/ Color.Black /*else Color.Gray*/,
            modifier = null
        )

    }
}

@Composable
fun MessageSubSection(/*chatData: ChatListDataObject*/) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextComponent(
            value = "Se ha quedado buena tarde",
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
      /*  chatData.message.unreadCount?.also {
            Text(
                text = chatData.message.unreadCount.toString(),
                modifier = Modifier
                    .padding(4.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onBackground),
                style = TextStyle(
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            )
        } */


    }
}


@Composable
fun TextComponent(
    value: String,
    fontSize: TextUnit,
    color: Color,
    modifier: Modifier?,
    fontWeight: FontWeight? = FontWeight.Normal
) {
    if (modifier != null) {
        Text(
            modifier = modifier,
            text = value,
            style = TextStyle(
                fontSize = fontSize,
                color = color,
                fontWeight = fontWeight
            )
        )
    } else {
        Text(
            text = value,
            style = TextStyle(
                fontSize = fontSize,
                color = color,
                fontWeight = fontWeight
            )
        )
    }
}

@Composable
private fun ConversationsTopBar(modifier: Modifier = Modifier) {

    var selectedTabIndex by remember { mutableStateOf(0) }
    // Calcula el número total de mensajes no leídos
    //val unreadMessagesCount = chatList.sumOf { it.message.unreadCount ?: 0 }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        ConstructAppLogo(
            modifier = Modifier
                .size(dimensionResource(R.dimen.topbar_logo_size))
                .padding(start = dimensionResource(R.dimen.topbar_logo_padding_start)),
            color = Color.Blue
        )
        TabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = { Box {} },
            modifier = Modifier.weight(1f)
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Chats",
                            color = Color.Blue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
//                        if (unreadMessagesCount > 0) {
//                            Text(
//                                text = unreadMessagesCount.toString(),
//                                color = Color.White,
//                                modifier = Modifier
//                                    .padding(4.dp)
//                                    .size(22.dp)
//                                    .clip(CircleShape)
//                                    .background(MaterialTheme.colorScheme.onBackground)
//                            )
//                        }
                    }
                }
            )
        }
//        UserProfileImage(
//            drawableResource = LocalAccountsDataProvider.defaultAccount.avatar,
//            description = stringResource(R.string.profile),
//            modifier = Modifier
//                .padding(end = dimensionResource(R.dimen.topbar_profile_image_padding_end))
//                .size(dimensionResource(R.dimen.topbar_profile_image_size))
//        )
    }
}

//@Composable
//@Preview(showBackground = true)
//fun ChatsScreenPreview() {
//    // Sample data for preview
//    val sampleUiState = ConstructAppUiState(
//        currentMailbox = MailboxType.Inbox
//    )
//
//    val sampleNavigationItems = listOf(
//        NavigationItemContent(
//            icon = Icons.Default.Home,
//            text = "Home",
//            mailboxType = MailboxType.Inbox
//        ),
//        NavigationItemContent(
//            icon = Icons.Default.Email,
//            text = "Messages",
//            mailboxType = MailboxType.Sent
//        )
//    )
//
//    val onTabPressed: (MailboxType) -> Unit = {}
//
//    ChatsScreen(
//        constructAppUiState = sampleUiState,
//        onTabPressed = onTabPressed,
//        navigationItemContentList = sampleNavigationItems
//    )
//}
