Efficient Raycasting for 2d Tilemaps
====================================

Java implemetation of the approximate ray/beam casting algorithm
for a discrete 2D tilemaps.


WUT?
----

For a square 2D map that consists of tiles (NxN tiles), where
each tile can be either empty, or contain light source or obstacle,
this algorithm calculates approximate brightness of each tile,
taking into account positions of the light sources
and occlusion from the obstacles.

![result.png](result.png)

(image was generated with `LightingTest`)


See also:
---------


* [JS demo](https://s3-us-west-1.amazonaws.com/plumbus.project/demo/2d-raycasting/index.html)
* [More detailed description](https://github.com/CleverRaven/Cataclysm-DDA/issues/23996#issue-331403618) by Kevin Granade

Properties
----------

If the input field is NxN tiles:

* worst case `O(N³)` (for the number of light sources between N and N²)
* best case is `O(N²)` (for constant number of light sources)
* CPU cache friednly (all data structures are accessed sequentially)


Why?
----

Naive ray/shadowcasting implementation is `O(N⁴)` in worst case
(`O(N²)` for each of `O(N²)` light sources).
This algorithm is  `O(N³)` worst case  (and it really matters when N >= 100).

Performance
-----------

See [benchmarks](benchmarks.md).



Licence
-------

Copyright © 2018 [Ivan Zaitsev](https://github.com/Aivean/)

[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/Aivean/efficient-2d-raycasting/blob/master/LICENSE)