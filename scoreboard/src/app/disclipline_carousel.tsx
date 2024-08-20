import React, { useState } from 'react';
import Carousel from 'react-material-ui-carousel';
import DisciplineModal from './discipline_modal';
import Table from "./table";


export default function DisciplineCarousel() {
    const disciplines = [
        {
          label: 'Disziplin 1',
          table: <Table/>,
        },
        {
          label: 'Disziplin 2',
          table: <Table/>,
        },
        {
          label: 'Disziplin 3',
          table: <Table/>,
        },
        {
          label: 'Disziplin 4',
          table: <Table/>,
        },
      ];
    return(
        <Carousel
        stopAutoPlayOnHover={false}
        animation = "slide"
        interval={3000}
        >
        {
            disciplines.map((item, index) => {
                return <DisciplineModal label={disciplines[index].label}/>
            })
        }
        </Carousel>
    )
}






