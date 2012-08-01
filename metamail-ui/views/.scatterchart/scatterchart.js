/*
var xdata = [5, 10, 15, 20],
    ydata = [3, 17, 4, 6];
*/
var xdata = [1998,1998,1998,1999,1999,1999,2000,2000,2000,2001,2001,2001,2002,2002,2002,2003,2003,2003];
var ydata = [];
for (var i = 1998; i <= 2003; i++) {
    for (var j = 0; j < 3; j++) {
        ydata.push(Math.floor(Math.random()*101));
    }
}


var margin = {top: 20, right: 15, bottom: 60, left: 60}
    , width = 960 - margin.left - margin.right
    , height = 500 - margin.top - margin.bottom;

var x = d3.scale.linear()
    .domain([d3.min(xdata), d3.max(xdata)])
    .range([ 0, width ]);

var y = d3.scale.linear()
    .domain([0, d3.max(ydata)])
    .range([ height, 0 ]);

var chart = d3.select('body')
    .append('svg:svg')
    .attr('width', width + margin.right + margin.left)
    .attr('height', height + margin.top + margin.bottom)
    .attr('class', 'chart')

var main = chart.append('g')
    .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')')
    .attr('width', width)
    .attr('height', height)
    .attr('class', 'main')   

// draw the x axis
var xAxis = d3.svg.axis()
    .scale(x)
    .orient('bottom');

main.append('g')
    .attr('transform', 'translate(0,' + height + ')')
    .attr('class', 'main axis date')
    .call(xAxis);

// draw the y axis
var yAxis = d3.svg.axis()
    .scale(y)
    .orient('left');

main.append('g')
    .attr('transform', 'translate(0,0)')
    .attr('class', 'main axis date')
    .call(yAxis);

var g = main.append("svg:g"); 

g.selectAll("scatter-dots")
    .data(ydata)
    .enter().append("svg:circle")
    .attr("cy", function (d) { return y(d); } )
    .attr("cx", function (d,i) { return x(xdata[i]); } )
    .attr("r", 10)
    .style("opacity", 0.6);
