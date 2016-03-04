
var
    gulp = require('gulp'),
    del  = require('del'),
    jade = require('gulp-jade'),
    concat = require('gulp-concat'),
    watch = require('gulp-watch'),
    rsync = require('gulp-rsync');

var jslibs = [
    'front/bower_components/angular/angular.min.js',
    'front/bower_components/angular-bootstrap/ui-bootstrap.min.js',
    'front/bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js'
];

var js = [
    'front/js/main.js',
    'front/js/controllers/*.js',
    'front/js/services/*.js'
];

var css = [
    'front/bower_components/bootstrap/dist/css/bootstrap.min.css',
    'front/bower_components/bootstrap/dist/css/bootstrap-theme.min.css',
    'front/bower_components/angular-bootstrap/ui-bootstrap-csp.css',
    'front/main.css'
];

var html = [
    'front/*.jade'
];

var templates = [
    'front/templates/*.jade'
];

var builddir = '../build/';

gulp.task('css', function(){
    gulp.src(css)
    .pipe(concat('main.css'))
    .pipe(gulp.dest(builddir + 'css/'));
})

gulp.task('libs', function(){
    gulp.src(jslibs)
    .pipe(concat('libs.js'))
    .pipe(gulp.dest(builddir + 'js'));
});

gulp.task('js', function(){
    gulp.src(js)
    .pipe(concat('main.js'))
    .pipe(gulp.dest(builddir + 'js'))
})

gulp.task('clean', function() {
    del.sync(builddir, {force: true});
})

gulp.task('html', function(){
    gulp.src(html)
    .pipe(jade())
    .pipe(gulp.dest(builddir));
});

gulp.task('templates', function() {
    gulp.src(templates)
    .pipe(jade())
    .pipe(gulp.dest(builddir + 'templates'));
})

gulp.task('watch', function(){
    watch(html, () => {gulp.start('html')});
    watch(js, () => {gulp.start('js')});
    watch(templates, () => {gulp.start('templates')});
});

gulp.task('deploy:front', function() {
    gulp.src(builddir + '**')
    .pipe(rsync({
        root: builddir,
        hostname: 'vlexz.net',
        destination: 'gurps'
    }))
});

gulp.task('deploy:server', function(){
    gulp.src('backend/**/')
    .pipe(rsync({
        root: 'backend',
        hostname: 'vlexz.net',
        destination: 'gurps-backend'
    }))
});

gulp.task('deploy', function() {
    
});

gulp.task('build', [
        'clean',
        'css',
        'libs',
        'js',
        'html',
        'templates'
    ]);