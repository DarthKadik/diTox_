// placeholder data for the frontend
const peers = ["54niufnd","4432","r44","1212","34","235","3345"];
const lastMessages =  {
    "54niufnd": {
        "message":"Lorem ipsum",
        "timestamp": 1667683809053-1000000000
    },
    "4432": {
        "message":"Dolor sit amet",
        "timestamp": 1667683809053
    },
    "r44": {
        "message":"Loremipsueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeem",
        "timestamp": 1667683809053-100000000
    },
    "235": {
        "message":"Lorem ipsum",
        "timestamp": 1667683809053
    }
}
const noMessagesYet = peers.filter(x => !Object.keys(lastMessages).includes(x));

/* Updating the UI */

/* Generating list of new contacts */
const newPeersCont = document.querySelector(".new-people-list");
for (var i = 0; i < noMessagesYet.length; i++) {
    const newElem = createElementFromHTML('<a href="./user-chat.html?uid=' + noMessagesYet[i] + '"><div class="person" style="' + generateGradient(noMessagesYet[i]) + ';"></div></a>');
    newPeersCont.appendChild(newElem);
}

/* Generating list of last messages */
const maxNumOfChars = 25;
const lastMessagesCont = document.querySelector(".main-page-content");
for (var i = 0; i < Object.keys(lastMessages).length; i++) {
    const userId = Object.keys(lastMessages)[i];
    var extract = lastMessages[userId]['message']
    if (extract.length > maxNumOfChars) {
        extract = extract.slice(0,maxNumOfChars) + '...';
    }

    const newElem = createElementFromHTML('<a href="./user-chat.html?uid=' + userId + '"><div class="message tile"><div class="person" style="' + generateGradient(userId) + ';"></div><div class="message-body"><h3>' + generateUName(userId) + '</h3><p class="time-ago">' + timeDifference(Date.now(), lastMessages[userId]['timestamp']) + '</p><p>' + extract + '</p></div><div class="arrow"></div></div></a>');
    lastMessagesCont.appendChild(newElem);
}