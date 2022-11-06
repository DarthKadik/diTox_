function createElementFromHTML(htmlString) {
    var div = document.createElement('div');
    div.innerHTML = htmlString.trim();

    return div.firstChild;
  }

function timeDifference(current, previous) {

  var msPerMinute = 60 * 1000;
  var msPerHour = msPerMinute * 60;
  var msPerDay = msPerHour * 24;
  var msPerMonth = msPerDay * 30;
  var msPerYear = msPerDay * 365;

  var elapsed = current - previous;

  if (elapsed < msPerMinute) {
       return Math.round(elapsed/1000) + ' seconds ago';   
  }

  else if (elapsed < msPerHour) {
       return Math.round(elapsed/msPerMinute) + ' minutes ago';   
  }

  else if (elapsed < msPerDay ) {
       return Math.round(elapsed/msPerHour ) + ' hours ago';   
  }

  else if (elapsed < msPerMonth) {
      return 'approximately ' + Math.round(elapsed/msPerDay) + ' days ago';   
  }

  else if (elapsed < msPerYear) {
      return 'approximately ' + Math.round(elapsed/msPerMonth) + ' months ago';   
  }

  else {
      return 'approximately ' + Math.round(elapsed/msPerYear ) + ' years ago';   
  }
}

function generateGradient(userId) {
  const rand1 = userId.charCodeAt(0)%3;
  const rand2 = userId.charCodeAt(1)%3;
  const hue = rand1*120 + rand2*40;

  return "background-image: url('./style/inc/user_profile.png'), linear-gradient(319deg, hsl(" + (hue-20) + ", 50%, 52%) 0%, hsl(" + (hue+20) + ", 50%, 52%) 100%);"
}

function generateUName(userId) {
  const rand1 = userId.charCodeAt(0)%3;
  const rand2 = userId.charCodeAt(1)%3;
  const hue = rand1*120 + rand2*40;

  var color;
  if (hue < 30) {
      color = "Red";
  } else if (hue < 47) {
      color = "Orange";
  } else if (hue < 65) {
      color = "Yellow";
  } else if (hue < 165) {
      color = "Green";
  } else if (hue < 255) {
      color = "Blue";
  } else if (hue < 292) {
      color = "Purple";
  } else if (hue < 337) {
      color = "Magenta";
  } else {
      color = "Red";
  }

  const animals = ["Rabbit","Elephant","Giraffe","Lion","Zebra","Horse","Cat","Dog","Turtle","Bear","Owl","Frog","Hamster","Koala","Llama","Kangaroo","Panda","Reindeer"];

  const animal = animals[hue%18];
  return color + ' ' + animal;
}

function addCSS(css) {
    head = document.head || document.getElementsByTagName('head')[0],
    style = document.createElement('style');

    head.appendChild(style);

    style.type = 'text/css';
    if (style.styleSheet){
    style.styleSheet.cssText = css;
    } else {
    style.appendChild(document.createTextNode(css));
    }
}

function generateBgColor(userId) {
    const rand1 = userId.charCodeAt(0)%3;
    const rand2 = userId.charCodeAt(1)%3;
    const hue = rand1*120 + rand2*40;

    return "hsla(" + hue + ", 50%, 52%, 0.7)";
}

// Interface for the app

function getMoods() {
  return JSON.parse(localStorage.getItem("moods"));
}