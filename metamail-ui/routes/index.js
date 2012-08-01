/*
 * Configure all routes
 */

exports.index = function(req, res){
  res.render('index', { title: 'Express' })
};

exports.msg_by_size = function(req, res) {
    res.render('graphics/msg-by-size.ejs', {
        title: 'Messages by Size',
        css_files: ['histogram.css'],
        csv_file: 'msg-by-size.csv'
    });
}

exports.msg_by_day_week = function(req, res) {
    res.render('graphics/histogram.ejs', {
        title: 'Messages by Day of Week',
        css_files: ['histogram.css'],
        csv_file: 'msg-by-day-week.csv'
    });
}

exports.msg_by_hour_day = function(req, res) {
    res.render('graphics/histogram.ejs', {
        title: 'Messages by Hour of Day',
        css_files: ['histogram.css'],
        csv_file: 'msg-by-hour-day.csv',
        w: 820,
        h: 320
    });
}

exports.msg_by_month = function(req, res) {
    res.render('graphics/histogram.ejs', {
        title: 'Messages by Month (Year 2001)',
        css_files: ['histogram.css'],
        csv_file: 'msg-by-month-2001.csv',
        w: 820,
        h: 320
    });
}

exports.msg_by_year = function(req, res) {
    res.render('graphics/msg-by-year.ejs', {
        title: 'Messages by Year',
        css_files: ['histogram.css'],
        csv_file: 'msg-by-year.csv'
    });
}

exports.msg_received = function(req, res) {
    res.render('graphics/bar.ejs', {
        title: 'Messages Received',
        css_files: ['bar.css'],
        csv_file: 'msg-received.csv',
        w: 820
    });
}

exports.msg_sent = function(req, res) {
    res.render('graphics/bar.ejs', {
        title: 'Messages Sent',
        css_files: ['bar.css'],
        csv_file: 'msg-sent.csv',
        w: 820
    });
}

exports.msg_thread_length = function(req, res) {
    res.render('graphics/bar.ejs', {
        title: 'Messages by Thread Length',
        css_files: ['bar.css'],
        csv_file: 'msg-thread-length.csv',
        w: 820
    });
}
