var w = 80;
var h = 80;

var startIntensity = 1 / (w * 2);

var game = new Phaser.Game(720, 720, Phaser.AUTO, 'phaser-example', {
    preload: preload,
    create: create,
    update: update,
    render: render
});

function preload() {
    game.load.spritesheet('tiles', 'img/tiles.jpg', 32, 32);
}

var marker;
var debugMarker;
var debugLight;

var renderTexture;
var outputSprite;
var drawSprite;
var sx, sy;
var click;

var lightHint = "";
var fullHint = '';

/* currently rendered tiles */
var tiles = [];
while (tiles.push(new Array(h)) < w) ; // first dimension is x, second is y

var tileBrightness = [];
while (tileBrightness.push(new Array(h)) < w) ; // first dimension is x, second is y

/* objects on the map (input)
    0 - empty space
    1 - light source
    2 - obstacle
*  */
var objects = [];
(function () {
    while (objects.push(new Array(h)) < w) ; // first dimension is x, second is y
    for (x = 0; x < w; x++) {
        for (y = 0; y < h; y++) {
            objects[x][y] = 0;
        }
    }
})();

/* light intensity (output) */
var light = [];
while (light.push(new Array(h)) < w) ; // first dimension is x, second is y


/* temp array for lighting calculation */
var tmp = [];
var tmp2 = [];
var tmp2IntN = 0;
var tmpNew = [];
var tmpNewIntN = 0;

// while (tmp.push(new Array(w * 2)) < w * 2) ; // first dimension is x, second is slope

function create() {
    renderTexture = game.add.renderTexture(game.width * 2, game.height * 2, 'ground');
    sx = renderTexture.width / w;
    sy = renderTexture.height / h;

    game.world.scale.setTo(game.width / renderTexture.width, game.height / renderTexture.height);

    outputSprite = game.add.sprite(0, 0, renderTexture);

    drawSprite = game.make.sprite(0, 0, 'tiles');
    drawSprite.scale.setTo(sx * renderTexture.width / game.width / 32, sy * renderTexture.width / game.width / 32);

    for (var x = 0; x < w; x++) {
        for (var y = 0; y < h; y++) {
            setTile(x, y, 0);
        }
    }

    marker = game.add.graphics();
    marker.lineStyle(1, 0xff0000, 0.8);
    marker.drawRect(0, 0, sx, sy);

    //recalculateLighting();
    redrawTiles();
}

function setTile(x, y, id, brightness) {
    brightness = brightness || 0.4;
    if (tiles[x][y] !== id || tileBrightness[x][y] !== brightness) {
        tiles[x][y] = id;
        tileBrightness[x][y] = brightness;
        drawSprite.frame = id;
        drawSprite.tint =
            (Math.floor(0xff * brightness) << 8 | Math.floor(0xff * brightness)) << 8 | Math.floor(0xff * brightness);
        renderTexture.renderXY(drawSprite, sx * x, sy * y);
    }
}

function getTile(x, y) {
    return tiles[x][y];
}

function update() {
    var x = Math.floor(game.input.activePointer.worldX / game.world.scale.x / sx);
    var y = Math.floor(game.input.activePointer.worldY / game.world.scale.y / sy);

    if (x >= 0 && x < w && y >= 0 && y < h) {
        lightHint = "" + light[x][y];
    }

    marker.x = x * sx;
    marker.y = y * sy;

    var shift = game.input.keyboard.isDown(Phaser.Keyboard.SHIFT);

    if (game.input.mousePointer.isDown) {
        if (x >= 0 && x < w && y >= 0 && y < h) {
            if (!click || click.x !== x || click.y !== y || click.shift !== shift) {
                if (!click) {
                    click = {
                        x: x,
                        y: y,
                        t: (objects[x][y] + 2) % 3,
                        shift: shift
                    }
                } else {
                    click.x = x;
                    click.y = y;
                    click.shift = shift;
                }

                var drawW = shift ? 2 : 0;
                var changeFlag = false;

                for (var dx = -drawW; dx <= drawW; dx++) {
                    for (var dy = -drawW; dy <= drawW; dy++) {
                        if (x + dx < 0 || x + dx >= w || y + dy < 0 || y + dy >= h) continue;
                        if (objects[x + dx][y + dy] !== click.t) {
                            objects[x + dx][y + dy] = click.t;
                            changeFlag = true;
                        }
                    }
                }
                if (changeFlag) {
                    recalculateLighting(redrawTiles);
                    // redrawTiles();
                }
            }
        }

        // if (objects[x][y])
        //     setTile(x, y, 6);
        // console.log(x, y);
    } else {
        click = null;
    }

    // if (game.input.keyboard.isDown(Phaser.Keyboard.SPACEBAR)) {
    //     if (!debugMarker) {
    //         debugMarker = {x: x, y: y};
    //         recalculateLighting(redrawTiles)
    //     }
    // } else {
    //     debugMarker = undefined;
    // }

    if (game.input.keyboard.isDown(Phaser.Keyboard.ENTER)) {
        if (!debugLight) {
            debugLight = [];
            for (var y1 = y; y1 < h; y1++) {
                debugLight.push(light[x][y1]);
            }
            console.log(debugLight.join())
        }
    } else {
        debugLight = undefined;
    }

    //
    // if (game.input.mousePointer.isDown) {
    //     var x = layer.getTileX(marker.x);
    //     var y = layer.getTileY(marker.y);
    //     var t = map.getTile(x, y);
    //     console.log(x, y, t);
    //     // map.putTile(1, x, y, layer);
    //     if (t) {
    //         var i = t.index;
    //         map.putTile((i + 1) % 14, x, y);
    //         console.log(t);
    //     }
    // }
}

function render() {
    var y = 32;
    var dy = 0;
    ['Left-click to paint',
        'Shift to batch-paint',
        'Enter to dump lighting to console',
        fullHint,
        lightHint
    ].forEach(function (v) {
        game.debug.text(v, 32, y + (dy++) * 16, '#efefef');
    });
}

function redrawTiles() {
    for (x = 0; x < w; x++) {
        for (y = 0; y < h; y++) {
            var t;
            var o = objects[x][y];
            if (o === 0) {
                t = (7 * x * y + x * 13 + y * 17) % 5;
            } else if (o === 1) { // light source
                t = 5;
            } else { // obstacle
                t = 6;
            }

            if (light[x][y] === 0) {
                t += 7;
            }

            // setTile(x, y, t, Math.min(1, Math.log(light[x][y] + 1) / 5 + 0.4));
            setTile(x, y, t, Math.min(1, light[x][y] / 100 + 0.4));
        }
    }
}

function recalculateLighting(cb) {
    var startDate = new Date();
    var raysTotalN = 0;


    var first = true
    var s = ""
    for (y = 0; y < h; y++) {
        for (x = 0; x < w; x++) {
            if (!first) s += ","

            s += objects[x][y]

            first = false
        }
    }

    fetch('light', {
        method: 'POST', // *GET, POST, PUT, DELETE, etc.
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        headers: {
            'Content-Type': 'text/plain'
        },
        body: s
    }).then(response => response.json())
        .then(data => {
            for (y = 0; y < h; y++) {
                for (x = 0; x < w; x++) {
                    light[x][y] = data[x][y];
                }
            }
            cb && cb()
        })

    fullHint = "render:" + (new Date() - startDate) + "ms;  rays N:" + (raysTotalN);
}
