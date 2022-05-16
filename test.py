from run import run


def test_single():
    run(True,"./book_cover.jpg")

def test_directory():
    run("false","img/")