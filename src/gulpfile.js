
var
    gulp = require('gulp'),
    del  = require('del'),
    jade = require('gulp-jade'),
    concat = require('gulp-concat');

var jslibs = [
    'front/bower_components/angular/angular.min.js'
];

var css = [
    'front/bower_components/bootstrap/dist/css/bootstrap.min.css',
    'front/bower_components/bootstrap/dist/css/bootstrap-theme.min.css',
    'front/main.css'
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

gulp.task('clean', function() {
    del.sync(builddir, {force: true});
})

gulp.task('html', function(){
    gulp.src(['front/*.jade'])
    .pipe(jade())
    .pipe(gulp.dest(builddir));
});

gulp.task('build', [
        'clean',
        'css',
        'libs',
        'html'
    ]);