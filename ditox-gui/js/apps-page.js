var statElemOne = document.querySelector("el chart");
const oneWH = window.innerHeight*0.01;
const moodScores = JSON.parse(localStorage.getItem("moods")).slice(-7);
const roundTimes = [0, 10, 15, 30, 45, 60];

//const names = Android.getAppNames();
//const icons = Android.getAppIcons();
//const avgTimes = Android.getAvgTimes();


const names = ["Instagram","Telegram","Headspace","Youtube","Spotify"];
const icons = ["./data/instagram-icon.png","./data/instagram-icon.png","./data/headspace-icon.png","./data/instagram-icon.png","./data/instagram-icon.png"];
const avgTimes = [[50,90,50,20,40,30,50],[30,50],[50,60,70,80,90,50,20,40,30,50],[3,5],[30,50,70,10,90,50,30]];
const avgInPastPeriod = [54,23,32,4,43];
const effects = ["negative","neutral","positive","negative","positive"];
const projectedIncrease = [0.13, false, false, 0.21, false];

const mainContentArea = document.querySelector(".main-page-content");
for (var i = 0; i < names.length; i++) {
    var newElement = createElementFromHTML('<div class="recommendation tile extended"><div class="el app-info"><div class="app-icon"></div><div class="txt"><h3>Instagram</h3><p>Effect on your wellbeing: <span class="effect"></span></p></div></div><div class="el chart"></div><div class="el rec"><h3 >App report:</h3><p>Your average screen time in the past 14 days was <span class="avg-screen-time"></span> mins</p><p class="rec-text"></p><div class="center"><button class="cta">Limit app usage</button></div></div></div>');    
    mainContentArea.appendChild(newElement);

    /* filling up element with usage data */
    newElement.querySelector(".txt h3").innerHTML = names[i];
    newElement.querySelector(".app-icon").style.backgroundImage = "url('"+icons[i]+"')";
    newElement.querySelector(".avg-screen-time").innerHTML = avgInPastPeriod[i];
    createGraphInsideElement(newElement,avgTimes[i].slice(-7));

    /* filling up element with recommendations */
    newElement.querySelector(".effect").innerHTML = effects[i];
    newElement.querySelector(".effect").classList.add(effects[i]);
    newElement.classList.add(effects[i]);

    if (effects[i] == "positive") {
        newElement.querySelector(".rec-text").innerHTML = "Based on your data, using this app has a positive impact on your wellbeing.";
    } else if (effects[i] == "neutral") {
        newElement.querySelector(".rec-text").innerHTML = "Based on your data, there isn't a significant connection between your app usage and wellbeing.";
    } else {
        const nextRoundTime = roundTimes.reverse().find((x) => {return x < avgInPastPeriod[i];});
        newElement.querySelector(".rec-text").innerHTML = "Based on your data, limiting your daily Instagram usage to <b>" + nextRoundTime + " minutes</b> would improve your mood by <b>" + projectedIncrease[i]*100 + "%</b>";
    }
}



/* Creating app inside tile */

function createGraphInsideElement(parent, screenTimes) {

    var canvas = document.createElement('canvas');
    canvas.width = (parent.offsetWidth - 2*oneWH);
    canvas.height = (20*oneWH);
    canvas.style.marginTop = oneWH + 'px';
    canvas.style.marginBottom = oneWH + 'px';
    parent.querySelector(".chart").appendChild(canvas);    

    const data = {
        labels: screenTimes,
        datasets: [
          {
            label: 'Wellbeing',
            data: moodScores,
            borderColor: [
                'rgb(162, 250, 238, 0.5)',
            ],
            backgroundColor: [
                'rgb(162, 250, 238, 0.5)',
            ],
            yAxisID: 'y',
            borderWidth: 6,
            lineTension: 0.4
          },
          {
            label: 'Screen Times',
            data: screenTimes,
            borderColor: [
                'rgb(48, 110, 101, 0.5)',
            ],
            backgroundColor: [
                'rgb(48, 110, 101, 0.5)',
            ],
            yAxisID: 'y1',
            borderWidth: 6,
            lineTension: 0.4
          }
        ]};

    const doubleChart = new Chart(canvas, {
        type: 'line',
        data: data,
        options: {
            scales: {
                x: {
                  grid: {
                    display: false,
                    drawBorder: false
                  },
                  ticks: {
                    display: false
                  }
                },
                y: {
                  grid: {
                    display: false,
                    drawBorder: false
                  },
                  ticks: {
                    display: false,
                    drawBorder: false,
                    }
                },
                y1: {
                    grid: {
                        display: false,
                        drawBorder: false
                    },
                    ticks: {
                      display: false,
                      drawBorder: false,
                      }
                },
            },
            plugins: {
                legend: {
                    display: false
                },
            }
        }
    });
}