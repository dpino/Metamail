
/**
 * Module dependencies.
 */

var express = require('express'),
    routes = require('./routes'),
    d3 = require('d3');

var app = module.exports = express.createServer();

// Configuration

app.configure(function(){
  app.set('views', __dirname + '/views');
  app.set('view engine', 'ejs');
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(app.router);
  app.use(express.static(__dirname + '/public'));
});

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
});

app.configure('production', function(){
  app.use(express.errorHandler());
});

// Routes

app.get('/', routes.index);
app.get('/graphics/msg-by-size', routes.msg_by_size);
app.get('/graphics/msg-by-day-week', routes.msg_by_day_week);
app.get('/graphics/msg-by-hour-day', routes.msg_by_hour_day);
app.get('/graphics/msg-by-month', routes.msg_by_month);
app.get('/graphics/msg-by-year', routes.msg_by_year);
app.get('/graphics/msg-received', routes.msg_received);
app.get('/graphics/msg-sent', routes.msg_sent);
app.get('/graphics/msg-thread-length', routes.msg_thread_length);

app.listen(3000, function(){
  console.log("Express server listening on port %d in %s mode", app.address().port, app.settings.env);
});
