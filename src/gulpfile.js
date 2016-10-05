
var
    gulp = require('gulp'),
    del  = require('del'),
    jade = require('gulp-jade'),
    concat = require('gulp-concat'),
    watch = require('gulp-watch'),
    rsync = require('gulp-rsync'),
    debug = require('gulp-debug'),
    series = require('gulp-sequence'),
    imagemin = require('gulp-imagemin');

console.log(series);

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

var images = [
    'front/images/*'
];

var builddir = '../build/';

gulp.task('css', function(){
    return gulp.src(css)
    .pipe(concat('main.css'))
    .pipe(gulp.dest(builddir + 'css/'));
})

gulp.task('libs', function(){
    return gulp.src(jslibs)
    .pipe(concat('libs.js'))
    .pipe(gulp.dest(builddir + 'js'));
});

gulp.task('js', function(){
    return gulp.src(js)
    .pipe(concat('main.js'))
    .pipe(gulp.dest(builddir + 'js'))
})

gulp.task('fonts', function(){
    return gulp.src('front/bower_components/bootstrap/dist/fonts/*')
    .pipe(debug({title: 'fonts'}))
    .pipe(gulp.dest(builddir + 'fonts'));
});

gulp.task('images', function(){
    return gulp.src(images)
    .pipe(imagemin({progressive: true}))
    .pipe(gulp.dest(builddir + 'images'));
});

gulp.task('clean', function() {
    return del.sync(builddir, {force: true});
})

gulp.task('html', function(){
    return gulp.src(html)
    .pipe(jade())
    .pipe(gulp.dest(builddir));
});

gulp.task('templates', function() {
    return gulp.src(templates)
    .pipe(debug({title: templates}))
    .pipe(jade())
    .pipe(gulp.dest(builddir + 'templates'));
})

gulp.task('watch', function(){
    watch(html, () => {gulp.start('html')});
    watch('front/includes/*', () => {gulp.start('html')});
    watch(js, () => {gulp.start('js')});
    watch(templates, () => {gulp.start('templates')});
    watch(css, () => {gulp.start('css')});
});

gulp.task('deploy:templates', function() {
    return gulp.src(['../build/templates/*', '../build/fonts/*'])
    .pipe(debug({title: 'templates'}))
    .pipe(rsync({
        root: '../build',
        hostname: 'vlexz.net',
        destination: 'gurps'
    }))
});

gulp.task('deploy:front', function() {
    return gulp.src(['../build/*', '../build/css/*', '../build/js/*', '../build/images/*'])
    .pipe(rsync({
        root: '../build',
        hostname: 'vlexz.net',
        destination: 'gurps'
    }))
});

gulp.task('deploy:server', function() {
    return gulp.src(['./backend/*', './backend/config/*', './backend/services/*'])
    .pipe(rsync({
        root: 'backend',
        hostname: 'vlexz.net',
        destination: 'gurps-backend'
    }))
});

gulp.task('build', series(
        'clean',
        'css',
        'libs',
        'js',
        'fonts',
        'html',
        'templates',
        'images'
    ));

gulp.task('deploy', series(
    'build',
    'deploy:templates',
    'deploy:front',
    'deploy:server'
    ));