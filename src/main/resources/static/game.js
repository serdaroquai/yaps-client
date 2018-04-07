// sendToServer({'type':'MOVE',id,x,y});
const log = console.log;

const updateGame= (() => {

	// last state (by using a closure we make sure updateGame is the only method that has access to state
	const currentState = 
		{
			'algo':'x17',
			'estimations':{},
			'blockChances':{},
			'estimationLabels':[]
		};
	
	
	return (message) => {
				
		// map whole stomp message to body which is the message
		if (message.type === "changeAlgo") {
			
			/*
			// update current selected Algo
			currentState.algo = message.selectedAlgo;

			// initialize the charts			
			charts.forEach((chart) => {
				chart.initialize(message);
				chart.update();
			});

			chartDifficulty.initialize(message);
			chartDifficulty.update();
			*/

		} else if (message.type === "algoUpdate") {

			/*
			// update the charts			
			charts.forEach((chart) => {
				chart.addData(message);
				chart.update();
			});
			*/
			
		} else if (message.type === "difficultyUpdate") {

			/*
			chartDifficulty.addData(message);
			chartDifficulty.update();
			*/
			
		} else if (message.type === "profitabilityUpdate") {
			
			// get latest data
			$.post("api/estimations",{}, function(response, status){
		        currentState.estimations = response;
		        // notify component
				chartEstimations.updateData(JSON.parse(JSON.stringify(currentState)));
				chartEstimations.update();
		    });

			/*
		    // get latest block chances
			$.post("api/blockChances",{}, function(response, status){
		        currentState.blockChances = response;

    			chartBlockChances.updateData(JSON.parse(JSON.stringify(currentState)));
				chartBlockChances.update();
		    });
		    */
			
		} else if (message.type === "minerUpdate") {
			
			document.getElementById("minerLog").innerHTML += "<div>"+ message.payload.line + "</div>";
			
		} else if (message.type === "estimationLabelsUpdate") {
			
			currentState.estimationLabels = message.payload.estimationLabels;
			chartEstimations.initializeLabels(JSON.parse(JSON.stringify(currentState)));
			chartEstimations.update();

			chartBlockChances.initializeLabels(JSON.parse(JSON.stringify(currentState)));
			chartBlockChances.update();

		} else if (message.type === "poolUpdate") {
			
			// get latest data
			$.post("api/poolStatus",{}, function(response, status){
		        currentState.poolUpdate = response;

    			chartPoolEstimations.updateData(JSON.parse(JSON.stringify(currentState)));
				chartPoolEstimations.update();
		    });

		}
	};
	
	
})();

const makeBarChart = (context, config) => {
	const chart = new Chart(context,config);
	
	chart.updateData = (newState) => {

		chart.data.datasets.forEach((dataset) => {
			
			//clear data
			dataset.data = [];

			//for each estimation create label if not exists
			for (const [algo, value] of Object.entries(newState.estimations)) {
				if (!chart.data.labels.includes(algo)) {
						chart.data.labels.push(algo)
					}
			}
			/*
			newState.estimations.forEach((estimation) => {
				if (!chart.data.labels.includes(estimation.algo)) {
					chart.data.labels.push(estimation.algo)
				}
			})
			*/

			//for each label pull data
			chart.data.labels.forEach((label) => {
	        	
	        	if (typeof newState.estimations[label] !== 'undefined') {
	        		dataset.data.push(newState.estimations[label]);
	        	} else {
	        		dataset.data.push(0)
	        	}
    		});
		});
	}

	chart.initializeLabels = (newState) => {
		chart.data.labels = newState.estimationLabels;
	}

	return chart;
}

const chartEstimations = makeBarChart(document.getElementById("estimationsChart").getContext('2d'), {
    type: 'horizontalBar',
    data: {
    	labels: [],
		datasets: [{
			label: "BTC / hashrate / day",
			borderColor:'green',
			borderWidth: 1,
			data: [],
			spanGaps: true,
		}]
    },
    options: {
    	scales: {
			xAxes: [{ticks: {
				suggestedMin: 0,
				suggestedMax: 0.001
			}}]
		},
    	title:{
    		display:true,
    		text:'Estimation x Hashrate'
    	}
    }
});


const makePoolEstimationsChart = (context, config) => {
	const chart = new Chart(context,config);
	
	chart.updateData = (newState) => {

		chart.data.datasets.forEach((dataset) => {
				
			//clear data
			dataset.data = [];
			var key = dataset.label;

			//for each label pull data
			chart.data.labels.forEach((label) => {
	        	
	        	if (typeof newState.poolUpdate[label] !== 'undefined') {
	        		dataset.data.push(newState.poolUpdate[label][key]);
	        	} else {
	        		dataset.data.push(0)
	        	}
    		});
		});
	}

	return chart;
}

const chartPoolEstimations = makePoolEstimationsChart(document.getElementById("poolEstimationsChart").getContext('2d'), {
    type: 'horizontalBar',
    data: {
    	labels: ["X17","PHI1612","Lyra2REv2","NeoScrypt","NIST5","Tribus","Xevan","X11Gost","Skein"],
		datasets: [{
			label: "estimateCurrent",
			borderColor:'blue',
			borderWidth: 1,
			data: [],
			spanGaps: true,
		},
		{
			label: "estimate24hr",
			borderColor:'red',
			borderWidth: 1,
			data: [],
			spanGaps: true,
		}]
    },
    options: {
    	scales: {
			xAxes: [{ticks: {
				suggestedMin: 0,
				suggestedMax: 0.001
			}}]
		},
    	title:{
    		display:true,
    		text:'Estimation x Hashrate'
    	}
    }
});


// buttons
document.getElementById('startProfitSwitching').addEventListener('click', function() {
	sendToServer({command:'setProfitSwitching', payload:true});
});
document.getElementById('stopProfitSwitching').addEventListener('click', function() {
	sendToServer({command:'setProfitSwitching', payload:false});
});
document.getElementById('X17').addEventListener('click', function() {
	sendToServer({command:'changeAlgo', payload:'X17'});
});
document.getElementById('PHI1612').addEventListener('click', function() {
	sendToServer({command:'changeAlgo', payload:'PHI1612'});
});       
document.getElementById('NeoScrypt').addEventListener('click', function() {
    sendToServer({command:'changeAlgo', payload:'NeoScrypt'});
});
document.getElementById('Lyra2REv2').addEventListener('click', function() {
    sendToServer({command:'changeAlgo', payload:'Lyra2REv2'});
});
document.getElementById('NIST5').addEventListener('click', function() {
    sendToServer({command:'changeAlgo', payload:'NIST5'});
});
document.getElementById('Tribus').addEventListener('click', function() {
    sendToServer({command:'changeAlgo', payload:'Tribus'});
});
document.getElementById('Xevan').addEventListener('click', function() {
    sendToServer({command:'changeAlgo', payload:'Xevan'});
});
document.getElementById('X11Gost').addEventListener('click', function() {
    sendToServer({command:'changeAlgo', payload:'X11Gost'});
});
document.getElementById('Skein').addEventListener('click', function() {
    sendToServer({command:'changeAlgo', payload:'Skein'});
});
document.getElementById('stopMiner').addEventListener('click', function() {
    sendToServer({command:'stopMiner'});
});
document.getElementById('clearScreen').addEventListener('click', function() {
    document.getElementById("minerLog").innerHTML="";
});




// connect to server
const sendToServer = ((endpoint, subscribeAdress, publishAddress, connectCallback, messageCallback) => {
	
	const stompClient = Stomp.over(new SockJS(endpoint));
	
	stompClient.connect({}, (frame) => {
		stompClient.subscribe(subscribeAdress, messageCallback);
		connectCallback();
	});
	
	return (message) => {
		stompClient.send(publishAddress, {}, JSON.stringify(message));
	}
	
})('/ws',
	'/topic/estimations',
	'/app/message',
	() => {
		// connect call back
	},
	(msg) => {
		updateGame(JSON.parse(msg.body));
	}); 