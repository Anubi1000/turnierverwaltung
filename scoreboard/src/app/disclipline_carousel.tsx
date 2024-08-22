import React, { useState, useEffect } from 'react';
import Carousel from 'react-material-ui-carousel';
import DisciplineModal from './discipline_modal';
import Table from "./table";


export default function DisciplineCarousel() {
  const disciplines = [
    {
      label: 'Disziplin 1',
      table: <Table moveNext={nextDiscipline}/>
    },
    {
      label: 'Disziplin 2',
      table: <Table moveNext={nextDiscipline}/>,
    },
    {
      label: 'Disziplin 3',
      table: <Table moveNext={nextDiscipline}/>,
    },
    {
      label: 'Disziplin 4',
      table: <Table moveNext={nextDiscipline}/>,
    },
  ];
  const [index, setIndex] = useState(0);
  /*useEffect(() => {
    let discplineTime = 5000;
    const interval = setInterval(async () => {
      if(index>=disciplines.length-1) {
        setIndex(0)
      } else {
        setIndex(index+1)
      }
    }, discplineTime)
    return () => {
      clearInterval(interval);
    }
  })*/

  function nextDiscipline() {
    if(index>=disciplines.length-1) {
      setIndex(0)
    } else {
      setIndex(index+1)
    }
  }
  return(
    <DisciplineModal key={index} label={disciplines[index].label} table={disciplines[index].table}/>
  )


}






