import sys
import gdb

# Update module path.
dir_ = '/home/gabriel/Downloads/MyNotes/fluid-synth/scripts/android-build-root/opt/android/x86_64/share/glib-2.0/gdb'
if not dir_ in sys.path:
    sys.path.insert(0, dir_)

from gobject_gdb import register
register (gdb.current_objfile ())
