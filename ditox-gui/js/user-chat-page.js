// Setup

const peerId = window.location.href.split('?uid=')[1]

const topBar = document.querySelector(".peer-header");
const newElem = createElementFromHTML('<div class="person" style="' + generateGradient(peerId) + ';"></div>');

// adding peer chat bubble color
var css = '.peer .chat-bubble { background-color: ' + generateBgColor(peerId) + '; }';
addCSS(css);

topBar.appendChild(newElem);

document.querySelector(".uname").innerHTML = generateUName(peerId);

// Loading messages

// const messages = Android.fetchMessages(peerId, "", "");
const ownId = "123";

// creating fake messages
var timestamp = Date.now()-(Math.random()*10000000);
var messages = []
for (var i = 0; i < 30; i++) {
    var from = ownId
    if (Math.round(Math.random())) {
        from = peerId
    }
    messages.push({
        "messageId":"54897",
        "sender":from,
        "receiver":"001",
        "timestamp": Math.floor(timestamp),
        "message": "Lorem ipsum dolor sit amet"
    });
}

// function for creating individual chat bubbles
const messCont = document.querySelector(".main-page-content");
function createBubble(messageJSON) {
    var from;
    if (messageJSON["sender"] == peerId) {
        from = "peer";
    } else {
        from = "user";
    }
    const newElem = createElementFromHTML('<div class="bubble-wrapper ' + from + '"><p class="chat-bubble">' + messageJSON["message"] + '</p></div>');
    messCont.appendChild(newElem);
}

// creating chat bubbles from previous messages
for (var i = 0; i < messages.length; i++) {
    createBubble(messages[i]);
}


// New message sent
function sendMessage() {
    const newMessage =  {
        "messageId":"000000",
        "sender":ownId,
        "receiver": peerId,
        "timestamp": Date.now(),
        "message": document.querySelector(".send-bar input").value,
    };

    createBubble(newMessage);
    window.scrollTo(0, document.querySelector(".main-page-content").scrollHeight);
    document.querySelector(".send-bar input").value = "";

    // Android.sendToId(peerId, newMessage, "");
}

// New message received
function messageReceived(message) {
    createBubble(message);
    window.scrollTo(0, document.querySelector(".main-page-content").scrollHeight);
};

// Scrolling to the bottom of messages
window.scrollTo(0, document.querySelector(".main-page-content").scrollHeight);