
var
    gulp = require('gulp'),
    del  = require('del'),
    jade = require('gulp-jade'),
    concat = require('gulp-concat'),
    watch = require('gulp-watch'),
    rsync = require('gulp-rsync'),
    debug = require('gulp-debug'),
    series = require('gulp-sequence'),
    imagemin = require('gulp-imagemin'),
    spawn =require('child_process').spawn;

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

gulp.task('build:scala', function(cb){
    let cmd = spawn('activator',['stage'], {
        cwd: 'scala-backend'
    })
    cmd.stdout.on('data', data => process.stdout.write(data));
    cmd.stderr.on('data', data => process.stderr.write(data));
    cmd.on('close', cb)
})

gulp.task('build:front:stage', function(cb){
    let cmd = spawn('ng',['build', '--env', 'stage'], {
        cwd: 'collaborative'
    })
    cmd.stdout.on('data', data => process.stdout.write(data));
    cmd.stderr.on('data', data => process.stderr.write(data));
    cmd.on('close', cb)
})

gulp.task('upload:front:stage', function() {
    return gulp.src('collaborative/dist/**')
    .pipe(rsync({
        root: 'collaborative/dist',
        hostname: 'vlexz.net',
        destination: 'gch-stage-front'
    }))
})

gulp.task('upload:scala', function(){
    return gulp.src('scala-backend/target/universal/stage/**')
    .pipe(rsync({
        root: 'scala-backend/target/universal/stage',
        hostname: 'vlexz.net',
        destination: 'scala-backend'
    }))
})

gulp.task('start:scala', function(cb) {
    let cmdpath = __dirname + '/scala-backend/target/universal/stage/bin/ggmtools';
    let cmd = spawn(cmdpath, ['-Dconfig.resource=dev.conf'], {
        cwd: 'scala-backend/target/universal/stage'
    })
    cmd.stdout.on('data', data => process.stdout.write(data));
    cmd.stderr.on('data', data => process.stderr.write(data));
    cmd.on('close', cb)
})

gulp.task('start:pug' ,function(cb){    
    let cmd = spawn('pug', ['watch', 'app', '--pretty', '--doctype', 'html'], {
        cwd: 'collaborative/src/'
    })
    cmd.stdout.on('data', data => process.stdout.write(data));
    cmd.stderr.on('data', data => process.stderr.write(data));
    cmd.on('close', cb)
});

gulp.task('start:dev', series(
    'build:scala',
    'start:scala'
))

gulp.task('deploy:stage', series(
    'build:front:stage',
    'upload:front:stage',
    'build:scala',
    'upload:scala'
))


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