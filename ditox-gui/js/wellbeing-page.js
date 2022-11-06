var now = new Date();
var fullDaysSinceEpoch = Math.floor(now/8.64e7);


// checking if can add new wellbeing score
if (fullDaysSinceEpoch <= JSON.parse(localStorage.getItem("last_added")) ) {
    // can't add new
    var main_text = '<h3>You already added a wellbeing score today. Check back in<br><span class="time-remaining"></span></h3>';
    document.querySelector(".wellbeing.tile").appendChild(createElementFromHTML(main_text));
    const updateTime = function () {
        var d = new Date();
        var h = d.getHours();
        var m = d.getMinutes();
        var s = d.getSeconds();
        var timeRemaining = (23-h).toLocaleString('en-US', {
            minimumIntegerDigits: 2,
            useGrouping: false
          }) + ':' + (59-m).toLocaleString('en-US', {
            minimumIntegerDigits: 2,
            useGrouping: false
          }) +':'+ (59-s).toLocaleString('en-US', {
            minimumIntegerDigits: 2,
            useGrouping: false
          });
        document.querySelector('.wellbeing.tile .time-remaining').innerHTML = timeRemaining;
    }
    updateTime();
    setInterval(updateTime,1000);
} else {
    // can add new
    var main_text = '<div class="score-box"><h1 class="emoji">🙂</h1><input type="range" min="1" max="100" oninput="rangeChange();"><br><button onClick="saveMood();">Save mood</button></div>';
    document.querySelector(".wellbeing.tile").appendChild(createElementFromHTML(main_text));
}

const emojis = ['😔','😐','🙂','😄','🤩']
function rangeChange() {
    var rangeVal = document.querySelector(".score-box input").value;
    document.querySelector(".score-box h1").innerHTML = emojis[Math.floor((rangeVal-1)/20)]
    
}

function saveMood() {
    var rangeVal = document.querySelector(".score-box input").value;

    var prevMoods;
    if (localStorage.getItem("moods")) {
        prevMoods = JSON.parse(localStorage.getItem("moods"));
    } else {
        prevMoods = [];
    }
    console.log(prevMoods);
    prevMoods.push(rangeVal)
    localStorage.setItem("moods",JSON.stringify(prevMoods));
    localStorage.setItem("last_added",JSON.stringify(fullDaysSinceEpoch))

    document.location.href = "./index.html"
}

function resetMoods() {
    localStorage.setItem("moods",JSON.stringify([76,43,23,68,54,23,67,87,67,56,89,45,98,56,76,45,81,30,87]));
    localStorage.setItem("last_added",JSON.stringify(19296));
    document.querySelector(".main-logo").style.opacity = "0.4";
};