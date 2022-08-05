#!/usr/bin/perl -s
use warnings;
use strict;
BEGIN { $^H |= 0x000000008 unless ($] < 5.006); } # "use bytes"

if ($h || $help) {
    die <<"EOF";
ppmascii v1.2 by cameron kaiser

options:
-showheader	show ppm height, width, etc.

distortions and transformations:
-x2		double pixels horizontally (averaged unless -nosmooth)
-y2		double pixels vertically (simple reduplication)
-winx=n		scroll window to horizonal pixel n
-winy=n		scroll window to vertical pixel n
-width=n	set width to n columns (default 79)
-height=n	stop after n rows

input-stage adjustments:
-bright=n	set bright to n (1.0=no change), 0<n<infinite (NOT gamma)
-contrast=n	set contrast to n (0=no change), -infinite<n<infinite

-photo		preset bright/contrast for continuous-tone and photo images
-lineart	preset bright/contrast for line art

rendering controls (by default RGB perceptual luminance with Floyd-Steinberg):
-invert		invert display to dark on light, not light on dark
-norgb		use arithmetic sum for luminance, not RGB perceptual coeffs
-nodither	do not use Floyd-Steinberg dithering (faster)
-nosmooth	do not use antialiasing (right now just -x2) (faster)

(c)2000, 2008, 2010 cameron kaiser all rights reserved word to your mother
*** http://www.floodgap.com/software/ppmascii/ ***
licensed under FFSL: www.floodgap.com/software/ffsl/
EOF
}

select(STDOUT); $|++;

@elements = (' ', qw(
    .
    ,
    :
    ;
    +
    =
    o
    a
    e
    O
    $
    @
    A
    #
    M
));

(@elements = reverse(@elements)) if ($invert);
$width ||= $ENV{'COLUMNS'} || 79;
$width += $winx;
$width += $winx if ($x2);
$bright ||= 1;
$bright = abs($bright);
$contrast ||= 0;

if ($photo) { $bright = 0.8; $contrast = -0.6; }
if ($lineart) { $bright = 1.0; $contrast = -300; }

binmode ARGV;
binmode STDIN;
&loadppm(\*ARGV);
&initppm;
print "$ppm_width $ppm_height $ppm_ccv $ppm_type\n" if ($showheader);
$pix = $ppm_width * $ppm_height;
$pix += $pix if ($x2);
$pix += $pix if ($y2);
$scf = (($ppm_ccv * 3) / scalar(@elements));
$pixx = $rows = $dwcc = $drows = 0;
$wcc = -1; # to start with
$ppm_ccv *= 1.5;
$this_row = '';
$diffscale = 768/scalar(@elements);
$lastlum = 0;
@empty = (); $#empty = $width;
@thisline = @empty; # for the floyd-steinberg algorithm
@nextline = @empty;
$lap = $llap = 0;

for(;;) {
    &widthfix;
    ($r, $g, $b) = &getnextpixel;
    #print "$r $g $b -- $wcc -- ";
    last if ($r == -1 || ($height && $drows >= $height));
    next if ($dwcc >= $width || $wcc < $winx || $rows < $winy);
    # normalized from 0.3R+0.59G+0.11B to the 3x we use here
    $olum = ($norgb) ? ($r + $g + $b) : (0.9*$r + 1.77*$g + 0.33*$b);
    unless ($nodither) {
        $lum = $olum+(7/16)*$lastlum+shift(@thisline);
    } else {
        $lum = $olum;
    }
    $cc = ($ppm_ccv - $lum) * $contrast;
    $cc += $lum; $cc=0 if ($cc<0);
    $llap = $lap;

    $lap = (!$scf) ? 0 : &clip($cc * $bright / $scf);
    unless ($nodither) {
        # setup matrix for f-s dither
        $lastlum = $olum-($diffscale*$lap);
        # remember that wcc
        $nextline[$wcc] += (5/16)*$lastlum;
        $nextline[$wcc+1] += (1/16)*$lastlum if ($wcc < $ppm_width);
        $nextline[$wcc-1] += (3/16)*$lastlum if ($wcc);
    }
    if ($x2 && $dwcc > 1 && !$nosmooth) {
        $elap = &clip(($lap + $llap) / 2);
    } else {
        $elap = $lap;
    }
    $j = ($elements[$elap]);
    $this_row .= $j;
    print $j;
    if ($x2) {
        $j = ($elements[$lap]);
        $this_row .= $j;
        print $j;
    }
}

exit;

sub widthfix {
    if (++$wcc == $ppm_width) {
        if ($rows >= $winy) {
            print "\n";
            print "$this_row\n" if ($y2);
            $drows++;
            $llap = 0;
        }
        $rows += 1 + $y2;
        $this_row = '';
        $dwcc = $wcc = $lastlum = 0;
        @thisline = @nextline;
        @nextline = @empty;
    }
    $dwcc += 1 + $x2;
}

sub clip {
    local $g = shift;
    $g = $#elements if ($g > $#elements);
    return int($g);
}

sub loadppm {
    local ($p, $w) = ($/, @_);

    undef $/;
    $ppm_buf = scalar(<$w>);
    return 1;
}

sub initppm {
    $ppm_buf =~ s/^\s*//s;
    ($ppm_type, $ppm_buf) = split(/\s+/s, $ppm_buf, 2);
    $ppm_buf =~ s/#[^\r\l\n]*[\r\l\n]+/\n/gs
        if ($ppm_type ne 'P6');
    $ppm_buf =~ s/^\s*//s;
    if ($ppm_type eq 'P1') { #pbm
        ($ppm_width, $ppm_height, $ppm_buf) =
            split(/\s+/s, $ppm_buf, 3);
        $ppm_ccv = 1;
    } else {
        ($ppm_width, $ppm_height, $ppm_ccv, $ppm_buf) =
            split(/\s+/s, $ppm_buf, 4);
    }
    $ppm_width += 0;
    $ppm_height += 0;
    $ppm_ccv += 0;
    $ppm_filepos = 0;
    return 1;
}

sub getnextcolour {
    local $nc;

    return -1 if (!length($ppm_buf) || $ppm_filepos >= length($ppm_buf));

    if ($ppm_type eq 'P3' || $ppm_type eq 'P2') {
        $ppm_buf =~ s/^\s*//s;
        ($nc, $ppm_buf) = split(/\s+/s, $ppm_buf, 2);
        return (0+$nc);
    } elsif ($ppm_type eq 'P6' || $ppm_type eq 'P5') {
        $nc = substr($ppm_buf, $ppm_filepos, 1);
        $ppm_filepos++;
        return 0+unpack("C", $nc);
    } elsif ($ppm_type eq 'P1') {
        $nc = substr($ppm_buf, 0, 1);
        $ppm_buf = substr($ppm_buf, 1);
        $ppm_buf =~ s/^[\r\l\n\s]*//s;
        return ($nc+0);
    } else {
        die("unsupported format $ppm_type");
    }
}

sub getnextpixel {
    local $r, $g, $b;
    if ($ppm_type eq 'P3' || $ppm_type eq 'P6') {
        $r = &getnextcolour;
        $g = &getnextcolour;
        $b = &getnextcolour;
    } else { # must be P1, P2 or P5
        $b = &getnextcolour;
        $r = $g = $b;
    }
    return (($r == -1 || $g == -1 || $b == -1) ? (-1, -1, -1) :
        ($r, $g, $b));
}

1;
