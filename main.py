from pickle import TRUE
import click
from run import run


@click.command()
@click.option('--flag', prompt = 'Flag',default = TRUE, help = 'Flag information')
@click.option('--input', prompt = 'Input_directory',default = './book_cover.jpg', help = 'Path to the input file')

def main(flag,input):
    run(flag,input)

if __name__ == '__main__':
    main()