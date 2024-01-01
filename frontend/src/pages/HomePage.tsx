import './styles/HomePage.scss';

function HomePage() {
    const cardData = [
        {
            title: 'Gamification Features',
            description: 'Track your progress in studying Kazakh poetry through a structured system of points and badges. Compare your performance with others on the leaderboard as you learn.',
        },
        {
            title: 'AI Poem Narration',
            description: 'Listen to Kazakh poems narrated by our AI. This feature provides a clear and accurate pronunciation guide to assist with your learning and appreciation of the poetry.',
        },
        {
            title: 'Custom Learning Path',
            description: 'The platform offers a personalized learning path that adjusts to your learning speed and style, facilitating a more tailored educational experience in Kazakh poetry.',
        },
        {
            title: 'Educational Games',
            description: 'Engage with built-in games that support the learning process. These games are designed to complement your study of Kazakh poetry through interactive challenges.',
        },
    ];

    return (
        <>
            <header
                className="container-fluid d-flex flex-column justify-content-center align-items-center gap-5 py-5">
                <div className="col-12 col-md-8 col-lg-6 text-center">
                    <h1 className="display-1 fw-light">Welcome to Poetry Club</h1>
                    <p className="lead">We are a team of talented designers</p>
                    <a href="#" className="btn btn-lg btn-primary">Get Started</a>
                </div>

                <div className="row">
                    {
                        Array.from({length: 4}).map((_, i) => (
                            <div className="col-xl-3 col-md-6 col-12" key={i}>
                                <div className="card border-0 shadow h-100 py-3 mb-3">
                                    <div className="card-body text-start">
                                        <h5 className="card-title mb-3">{cardData[i].title}</h5>
                                        <p className="card-text">{cardData[i].description}</p>
                                    </div>
                                </div>
                            </div>
                        ))
                    }
                </div>
            </header>

            <div id="carouselExampleIndicators" className="carousel slide">
                <div className="carousel-indicators">
                    <button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="0"
                            className="active" aria-current="true" aria-label="Slide 1"></button>
                    <button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="1"
                            aria-label="Slide 2"></button>
                    <button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="2"
                            aria-label="Slide 3"></button>
                </div>
                <div className="carousel-inner">
                    <div className="carousel-item active">
                        <img
                            src="https://ineu.edu.kz/uploads/posts/2020-11/1606132725_whatsapp-image-2020-11-23-at-11_47_24.jpeg"
                            className="d-block w-100" alt="..."/>
                        <div className="carousel-caption d-none d-md-block">
                            <h5><span>First slide label</span></h5>
                            <p><span>Some representative placeholder content for the first slide.</span></p>
                        </div>
                    </div>
                    <div className="carousel-item">
                        <img src="https://tekelinews.kz/uploads/news/3882/news3882.jpg" className="d-block w-100"
                             alt="..."/>
                        <div className="carousel-caption d-none d-md-block">
                            <h5><span>Second slide label</span></h5>
                            <p><span>Some representative placeholder content for the second slide.</span></p>
                        </div>
                    </div>
                    <div className="carousel-item">
                        <img src="https://i.ytimg.com/vi/YBSBVXd3djE/maxresdefault.jpg" className="d-block w-100"
                             alt="..."/>
                        <div className="carousel-caption d-none d-md-block">
                            <h5><span>Third slide label</span></h5>
                            <p><span>Some representative placeholder content for the third slide.</span></p>
                        </div>
                    </div>
                </div>
                <button className="carousel-control-prev" type="button" data-bs-target="#carouselExampleIndicators"
                        data-bs-slide="prev">
                    <span className="carousel-control-prev-icon" aria-hidden="true"></span>
                    <span className="visually-hidden">Previous</span>
                </button>
                <button className="carousel-control-next" type="button" data-bs-target="#carouselExampleIndicators"
                        data-bs-slide="next">
                    <span className="carousel-control-next-icon" aria-hidden="true"></span>
                    <span className="visually-hidden">Next</span>
                </button>
            </div>
        </>
    )
}

export default HomePage;
