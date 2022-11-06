var statElemOne = document.querySelector(".statistics.mood");
var statElemTwo = document.querySelector(".statistics.apps");
var oneWH = window.innerHeight*0.01;

var moodCanvas = document.createElement('canvas');
moodCanvas.id = "mood-chart";
moodCanvas.width = (statElemOne.offsetWidth - 2*oneWH);
moodCanvas.height = (32*oneWH);
moodCanvas.style.marginLeft = oneWH + 'px';
moodCanvas.style.marginTop = oneWH + 'px';
moodCanvas.style.marginBottom = oneWH + 'px';

statElemOne.appendChild(moodCanvas);

var appsCanvas = document.createElement('canvas');
appsCanvas.id = "apps-chart";
appsCanvas.width = (statElemOne.offsetWidth - 2*oneWH);
appsCanvas.height = (32*oneWH);
appsCanvas.style.marginLeft = oneWH + 'px';
appsCanvas.style.marginTop = oneWH + 'px';
appsCanvas.style.marginBottom = oneWH + 'px';

statElemTwo.appendChild(appsCanvas);


// mood chart

const days = ["Mon","Tue","Wed","Thu","Fri","Sat","Sun"]
const moodData = [4,5,4,6,8,7,9]
const moodChart = new Chart(moodCanvas, {
    type: 'line',
    data: {
        labels: days,
        datasets: [{
            label: 'mood',
            data: moodData,
            backgroundColor: [
                'rgba(162, 250, 238, 1)',
            ],
            borderColor: [
                'rgb(162, 250, 238, 0.5)',
            ],
            borderWidth: 6,
            lineTension: 0.4
        }]
    },
    options: {
        scales: {
            x: {
              grid: {
                display: false,
                drawBorder: false
              },
              ticks: {
                color: "rgb(240, 255, 253)",
                size: "100px"
              }
            },
            y: {
              grid: {
                color: "rgba(240, 255, 253, 0.2)",
              },
              ticks: {
                display: false,
                drawBorder: false,
                }
            }
        },
        plugins: {
            legend: {
                display: false
            },
        }
    }
});

var appUsageData = [];
// app usage chart
function coolTest() {
    document.getElementById("insight").innerHTML = "fasz ki van";
}

Android.showAppUsage();
function appDataUpdater(numbers, appNames) {
    appUsageData = numbers;

    Chart.overrides.polarArea.plugins.legend.position = "bottom";
    Chart.overrides.polarArea.plugins.legend.labels.color = "rgb(240, 255, 253)";

    const appUsageChart = new Chart(appsCanvas, {
      type: 'polarArea',
      data: {
        labels: appNames,
        datasets: [{
          label: "",
          data: appUsageData,
          backgroundColor: [
            'rgba(162, 250, 238, 0.7)',
            'rgba(162, 250, 238, 0.6)',
            'rgba(162, 250, 238, 0.5)',
            'rgba(162, 250, 238, 0.4)',
            'rgba(162, 250, 238, 0.3)'
          ]
        }]
      },
      options: {
        scales: {
          r: {
            ticks: {
              display: false // Remove vertical numbers
            },
            grid: {
              color: "rgba(240, 255, 253, 0.2)"
            }
          }
        },
        elements: {
          arc: {
            borderWidth: 0,
          }
        }
      }

    });
}


